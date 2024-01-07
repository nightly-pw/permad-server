package club.premiering.permad.entity;

import club.premiering.permad.math.AABoundingBox;
import club.premiering.permad.math.MathUtil;
import club.premiering.permad.math.Vector2;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.protocol.gameplay.ChatMessagePacketOut;
import club.premiering.permad.protocol.gameplay.LocalPlayerStatePacketOut;
import club.premiering.permad.protocol.gameplay.MoveDirection;
import club.premiering.permad.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EntityPlayer extends RigidEntity {
    private static final Random RANDOM = new Random();

    //Physics related constants
    private static final float PHYSICS_SCALE = 1.2f;// Mean for testing in the meantime, could be used in the future for a map element or something?
    private static final Vector2 PLAYER_SPEED = new Vector2(1.5f/PHYSICS_SCALE, 2.1f/PHYSICS_SCALE);//1.8 y
    private static final Vector2 PLAYER_SPRINT_SPEED = new Vector2(4.2f/PHYSICS_SCALE, 5f/PHYSICS_SCALE);
    private static final Vector2 GRAVITY_AMOUNT = new Vector2(0.25f/PHYSICS_SCALE, 0.45f/PHYSICS_SCALE);//X value is for counter velocity to stop infinte velocity on x axis (old y 0.65)
    private static final Vector2 PLAYER_MOVEMENT_MAX_VELOCITY = new Vector2(14f/PHYSICS_SCALE, 20f/PHYSICS_SCALE);//old y 20
    private static final float PLAYER_MAX_FALL_SPEED = 12f/PHYSICS_SCALE;//14
    private static final float PLAYER_MAX_FALL_SPEED_SPRINTING = 19.5f/PHYSICS_SCALE;
    private static final float MAX_GRAVITY_VEL_Y = 8f/PHYSICS_SCALE;//Min being how fast an object can go down due to gravity, this is separate from min vel
    private static final Vector2 DEAD_COUNTER_VEL = new Vector2(0.35f, 0.35f);
    private static final Vector2 DEAD_COLLISION_BOUNCE_VEL = new Vector2(9f, 9f);
    private static final float GOD_MOVEMENT_SPEED = 18f;

    //Boost constants
    private static final float BOOST_PER_TICK = 0.6f;//0.8f
    private static final float BOOST_PER_TICK_SPRINTING = 1.5f;
    private static final float BOOST_PER_TICK_CANCELLING = 20f;
    private static final float BOOST_REFILL_PER_TICK = 3f;

    // Used to give the seemings of circle collision, noted in lower TODO
    private static final float PLACEBO_CONST = 6f;

    public GameSession session;

    @Getter
    private String name = "Guest";

    @Getter
    private final Color color;
    @Getter
    private float boostAmount = 100f;//0 - 100
    @Getter
    private boolean dead;
    @Getter
    private boolean god = false;

    private boolean wasCancellingLastTick = false;

    private Set<MoveDirection> directions = new HashSet<>();

    public EntityPlayer(GameSession session) {
        super();

        this.session = session;
        this.size = new Vector2(48, 48);
        this.setPhysicsSettings(false);

        //Set random color
        final float hue = RANDOM.nextFloat();
        this.color = Color.getHSBColor(hue, 1f, 1f);
    }

    @Override
    public void doTick() {
        if (this.god) {
            this.processGodMovement();
            return;
        }

        if (!this.dead) {
            this.processMovement();
            this.processAlivePhysics();
        } else {
            this.processDeadPhysics();
        }
        this.applyVelocities();

        // Done ticking!
        super.doTick();
    }

    // TODO: Implement circle collision detection
    @Override
    public void createAABBs() {
        this.aabbs.add(new AABoundingBox(this.pos, this.size.clone().sub(PLACEBO_CONST, PLACEBO_CONST)));
    }
    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {
        if (entity instanceof EntityPlayer otherPlayer) {
            //Respawn both players since they're touching
            if (this.dead)
                this.respawnPlayer();
            if (otherPlayer.dead)
                otherPlayer.respawnPlayer();

            //Add boost since they're touching and reviving each other (div 2 because this will be called by both parties)
            this.setBoostAmount(this.getBoostAmount() + BOOST_REFILL_PER_TICK / 2);
            otherPlayer.setBoostAmount(otherPlayer.getBoostAmount() + BOOST_REFILL_PER_TICK / 2);
        }

        if (this.dead && entity.isSolid() && entity.isActive())
            this.bounceOffCollision(colInfo, DEAD_COLLISION_BOUNCE_VEL);
    }

    public void killPlayer(AABBCollisionInfo colInfo) {
        if (this.dead)
            return;

        this.dead = true;
        this.setOnGround(false);

        this.bounceOffCollision(colInfo, DEAD_COLLISION_BOUNCE_VEL);

        this.broadcastMetadataUpdate();
    }

    public void revivePlayer() {
        this.dead = false;
        this.broadcastMetadataUpdate();
    }

    public void respawnPlayer() {
        this.revivePlayer();
    }

    private void processMovement() {
        var playerSpeed = this.isSprinting() && this.boostAmount > 0 ? PLAYER_SPRINT_SPEED : PLAYER_SPEED;

        if (this.isCancelling() && !this.wasCancellingLastTick && this.boostAmount > 0) {
            this.velocity.x = 0;
            this.velocity.y = 0;
            this.setBoostAmount(this.boostAmount - BOOST_PER_TICK_CANCELLING);

            this.wasCancellingLastTick = true;
        } else if (!this.isCancelling()) {
            this.wasCancellingLastTick = false;
        }

        if (this.isMovingUp() && this.boostAmount > 0) {
            this.velocity.y -= playerSpeed.y;
            //Remove boost from the player
            if (this.isSprinting())
                this.setBoostAmount(this.boostAmount - BOOST_PER_TICK_SPRINTING);
            else
                this.setBoostAmount(this.boostAmount - BOOST_PER_TICK);
        }
        if (this.isMovingLeft()) {
            this.velocity.x -= playerSpeed.x;
        }
        if (this.isMovingDown() && this.boostAmount > 0) {
            this.velocity.y += playerSpeed.y;
        }
        if (this.isMovingRight()) {
            this.velocity.x += playerSpeed.x;
        }

        if (this.isOnGround() && this.boostAmount != 100) {
            this.setBoostAmount(this.boostAmount + BOOST_REFILL_PER_TICK);
        }
    }

    private void processAlivePhysics() {
        //Apply vertical gravity if we're in the air
        if (!this.isOnGround()) {
            this.velocity.y += GRAVITY_AMOUNT.y;

            if (this.velocity.y > MAX_GRAVITY_VEL_Y) {
                this.velocity.y = this.isMovingDown() ? (this.isSprinting() ? PLAYER_MAX_FALL_SPEED_SPRINTING : PLAYER_MAX_FALL_SPEED) : MAX_GRAVITY_VEL_Y;
            }
        }

        //Apply counter-velocity on x-axis (called drag?)
        if (this.velocity.x < 0) {
            this.velocity.x = Math.min(this.velocity.x + GRAVITY_AMOUNT.x, 0);
        } else {
            this.velocity.x = Math.max(this.velocity.x - GRAVITY_AMOUNT.x, 0);
        }

        //Clamp velocity to their movement maxes
        this.velocity.x = MathUtil.clamp(this.velocity.x, -PLAYER_MOVEMENT_MAX_VELOCITY.x, PLAYER_MOVEMENT_MAX_VELOCITY.x);
        this.velocity.y = MathUtil.clamp(this.velocity.y, -PLAYER_MOVEMENT_MAX_VELOCITY.y, PLAYER_MOVEMENT_MAX_VELOCITY.y);
    }

    private void processDeadPhysics() {
        //The goal is to apply *ONLY* counter velocity, no gravity or anything extra

        //Apply counter x
        if (this.velocity.x < 0) {
            this.velocity.x = Math.min(this.velocity.x + DEAD_COUNTER_VEL.x, 0);
        } else {
            this.velocity.x = Math.max(this.velocity.x - DEAD_COUNTER_VEL.x, 0);
        }

        //Apply counter y
        if (this.velocity.y < 0) {
            this.velocity.y = Math.min(this.velocity.y + DEAD_COUNTER_VEL.y, 0);
        } else {
            this.velocity.y = Math.max(this.velocity.y - DEAD_COUNTER_VEL.y, 0);
        }
    }

    private void applyVelocities() {
        this.pos.x += this.velocity.x;
        this.pos.y += this.velocity.y;
    }

    private void processGodMovement() {
        if (this.isMovingLeft())
            this.pos.x -= GOD_MOVEMENT_SPEED;
        if (this.isMovingUp())
            this.pos.y -= GOD_MOVEMENT_SPEED;
        if (this.isMovingRight())
            this.pos.x += GOD_MOVEMENT_SPEED;
        if (this.isMovingDown())
            this.pos.y += GOD_MOVEMENT_SPEED;
    }

    public void setBoostAmount(float boostAmount) {
        this.boostAmount = MathUtil.clamp(boostAmount, 0, 100);

        this.sendLocalStateUpdate();
    }

    private void setPhysicsSettings(boolean god) {
        this.setCanBeOutsideWorld(god);
        this.setCollideWithSolids(!god);
    }

    public void setGod(boolean god) {
        this.setPhysicsSettings(god);
        this.god = god;
    }

    public void sendLocalStateUpdate() {
        this.world.submitPacket(this.session, new LocalPlayerStatePacketOut(this));
    }

    public boolean isMovingUp() {
        return this.directions.contains(MoveDirection.W);
    }

    public boolean isMovingDown() {
        return this.directions.contains(MoveDirection.S);
    }

    public boolean isMovingLeft() {
        return this.directions.contains(MoveDirection.A);
    }

    public boolean isMovingRight() {
        return this.directions.contains(MoveDirection.D);
    }

    public boolean isSprinting() {
        return this.directions.contains(MoveDirection.SPRINT);
    }

    public boolean isCancelling() {
        return this.directions.contains(MoveDirection.CANCEL);
    }

    public void addDirectionState(MoveDirection dir) {
        this.directions.add(dir);
    }

    public void removeDirectionState(MoveDirection dir) {
        this.directions.remove(dir);
    }

    public void sendMessage(String s) {
        this.world.submitPacket(this.session, new ChatMessagePacketOut(s));
    }

    @Override
    public void writePacketEntityMetadata(ByteBuf buf) {
        super.writePacketEntityMetadata(buf);

        NettyUtils.writeString(buf, this.name);
        buf.writeBoolean(this.dead);
        buf.writeInt(this.color.getRGB());
    }
}
