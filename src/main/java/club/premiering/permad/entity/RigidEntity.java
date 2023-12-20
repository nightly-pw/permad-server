package club.premiering.permad.entity;

import club.premiering.permad.math.AABoundingBox;
import club.premiering.permad.math.BoundingBox;
import club.premiering.permad.math.MathUtil;
import club.premiering.permad.math.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

// This represents an entity which takes part in any way of physics in the game
// Currently the solution is very hacky and needs reworking, this should be a top priority.
// (Only calculates AABBs and (detection only) rotated AABBs)
public abstract class RigidEntity extends Entity {
    @Getter
    @Setter
    private boolean active = true;
    @Getter
    @Setter
    private boolean solid = false;//If other rigids touch us, do we stop them?
    @Getter
    @Setter
    private boolean collideWithSolids = false;//If we touch other rigids, do we stop?

    @Getter
    @Setter
    private boolean onGround = false;
    @Getter
    @Setter
    private boolean canBeOutsideWorld = true;

    @Getter
    private Collection<Entity> touchingEntities = new ArrayList<>();//The entities that this entity are currently touching
    protected Collection<AABoundingBox> aabbs = new ArrayList<>();

    public Vector2 velocity = new Vector2(0, 0);

    public BoundingBox getBoundingBox() {
        return new BoundingBox(this.pos.x, this.pos.y, this.size.x / 2, this.size.y / 2, Math.toRadians(this.rot));
    }

    public Collection<AABoundingBox> getAabbs() {
        this.updateAABBs();
        return this.aabbs;
    }

    public void createAABBs() {
        this.aabbs.add(new AABoundingBox(this.pos, this.size));
    }

    protected void updateAABBs() {
        if (this.lastPos != this.pos || this.lastSize != this.size) {
            this.aabbs.clear();
            this.createAABBs();
        }
    }

    @Override
    public void doTick() {
        this.updateAABBs();

        //Reset states
        if (this.velocity.x != 0f || this.velocity.y != 0f) {
            this.onGround = false;
        }
        this.touchingEntities.clear();

        //Clamp entity to world border
        if (!this.canBeOutsideWorld) {
            //Following if statements are for resolving onGround state and modifying velocity accordingly

            //Are we on the ground? If so remove velocity
            if (this.pos.y >= this.world.getWorldSize().w - this.size.y / 2) {
                this.onGround = true;
                this.velocity.y = 0;
            }
            //Are we touching the world size vertical walls
            if (this.pos.x <= this.world.getWorldSize().x + this.size.x / 2 || this.pos.x >= this.world.getWorldSize().z - this.size.x / 2) {
                this.velocity.x = 0;
            }
            //Are we touching the world roof?
            if (this.pos.y <= this.world.getWorldSize().y + this.size.y / 2) {
                this.velocity.y = 0;
            }
            this.pos.x = MathUtil.clamp(this.pos.x, this.world.getWorldSize().x + (this.size.x / 2) - 2, this.world.getWorldSize().z - this.size.x / 2 + 2);
            this.pos.y = MathUtil.clamp(this.pos.y, this.world.getWorldSize().y + (this.size.y / 2) - 2, this.world.getWorldSize().w - this.size.y / 2 + 2);
        }

        if (!this.active)
            return;

        Collection<AABoundingBox> ourAabbs = this.getAabbs();

        //Calculate collisions and resolve them
        for (Entity otherEntity : this.world.getEntities()) {
            if (otherEntity == this || !(otherEntity instanceof RigidEntity rEnt) || !rEnt.active)
                continue;

            this.processBBCollisions(rEnt);
            if (isNotStraight(rot) || isNotStraight(rEnt.rot)) {
                this.processBBCollisions(rEnt);
            } else {
                this.processAABBCollisions(rEnt, ourAabbs);
            }
        }
    }

    // Modulus was not working for some reason, maybe compiler or floating point related?
    private static boolean isNotStraight(float rot) {
        return rot != 0f;
    }

    private void processBBCollisions(RigidEntity rEnt) {
        BoundingBox a = this.getBoundingBox();
        BoundingBox b = rEnt.getBoundingBox();
        if (a.isCollidingWith(b)) {
            this.touchingEntities.add(rEnt);
            rEnt.touchingEntities.add(this);

            this.onCollision(rEnt, new AABBCollisionInfo(false, false, false, false));
            rEnt.onCollision(this, new AABBCollisionInfo(false, false, false, false));
        }
    }

    // Detects and offsets AABB collisions
    private void processAABBCollisions(RigidEntity rEnt, Collection<AABoundingBox> ourAabbs) {
        Collection<AABoundingBox> otherAabbs = rEnt.getAabbs();

        for (AABoundingBox a : ourAabbs) {
            for (AABoundingBox b : otherAabbs) {
                //Setup up vars for testing for a collision
                boolean right = a.x < b.x + b.width;
                boolean left = a.x + a.width > b.x;
                boolean bottom = a.y < b.y + b.height;
                boolean top = a.y + a.height > b.y;

                //Have we collided?
                if (left && top && bottom && right) {
                    boolean leftFace = false;
                    boolean rightFace = false;
                    boolean topFace = false;
                    boolean bottomFace = false;

                    float slope = (b.getCenter().x - a.getCenter().x) / (b.getCenter().y - a.getCenter().y);
                    float height = a.getCenter().y > b.getCenter().y ? b.height / 2 : -(b.height / 2);
                    float x = slope * height;
                    if (x > b.width / 2) {
                        rightFace = true;
                    } else if (x < -b.width / 2) {
                        leftFace = true;
                    }
                    if (height < 0) {
                        if (!leftFace && !rightFace) {
                            topFace = true;
                        }
                    } else if (height > 0) {
                        if (!leftFace && !rightFace) {
                            bottomFace = true;
                        }
                    }

                    var collisionInfo = new AABBCollisionInfo(leftFace, topFace, rightFace, bottomFace);

                    if (rEnt.isSolid() && this.collideWithSolids) {
                        if (leftFace) {
                            this.pos.x = b.getCenter().x - b.width / 2 - a.width / 2;
                            this.velocity.x = 0;
                        } else if (rightFace) {
                            this.pos.x = b.getCenter().x + b.width / 2 + a.width / 2;
                            this.velocity.x = 0;
                        } else if (topFace) {
                            this.pos.y = b.getCenter().y - b.height / 2 - a.height / 2;
                            this.velocity.y = 0;
                        } else if (bottomFace) {
                            this.pos.y = b.getCenter().y + b.height / 2 + a.height / 2;
                            this.velocity.y = 0;
                        }
                        this.setOnGround(topFace);
                    }

                    this.touchingEntities.add(rEnt);
                    rEnt.touchingEntities.add(this);

                    this.onCollision(rEnt, collisionInfo);
                    rEnt.onCollision(this, collisionInfo.copy().invert());
                }
            }
        }
    }

    public void bounceOffCollision(AABBCollisionInfo colInfo, Vector2 bounceVel) {
        if (colInfo.topFace) {
            this.velocity.y = -bounceVel.y;
        }
        if (colInfo.bottomFace) {
            this.velocity.y = bounceVel.y;
        }
        if (colInfo.leftFace) {
            this.velocity.x = -bounceVel.x;
        }
        if (colInfo.rightFace) {
            this.velocity.x = bounceVel.x;
        }
    }

    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {}
}
