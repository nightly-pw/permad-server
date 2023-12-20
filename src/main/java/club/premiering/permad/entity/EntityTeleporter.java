package club.premiering.permad.entity;

import club.premiering.permad.PermaGlobals;
import club.premiering.permad.math.Vector2;
import club.premiering.permad.world.World;
import club.premiering.permad.format.EntityMetadata;
import io.netty.buffer.ByteBuf;

public class EntityTeleporter extends RigidEntity {
    //Hard to name, but it's how far offset you are from the teleporter you teleport to, so here it's set to 2 pixels off of the teleporter
    private static final float TELEPORTER_BUFFER = 2f;

    //Metadata
    public int teleporterId;//Our id
    public int targetWorldId;//The id of the world we're tping to
    public int targetTeleporterId;//The target of the teleporter we're tping to
    public byte dir;

    public EntityTeleporter() {
        this.setSolid(false);
        this.setCollideWithSolids(false);
    }

    @Override
    public void doTick() {
        super.doTick();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {
        if (entity instanceof EntityPlayer player) {
            var targetWorld = this.getTargetWorldOrLoad();
            var targetTp = this.getTargetTeleporterOrLoad();
            if (targetWorld != null) {
                var pos = targetTp == null ? new Vector2(0, 0) : this.getNewPlayerPosition(targetTp, player);
                this.world.runNextTick(() -> {
                    if (this.world != targetWorld)
                        this.world.getRoom().switchPlayerWorld(player, targetWorld);

                    // If only Java was C
                    if (PermaGlobals.CONFIG.debugMode)
                        System.out.println("Found target teleporter " + targetTp);

                    player.pos = pos;
                });
            }
        }
    }

    private Vector2 getNewPlayerPosition(EntityTeleporter targetTp, EntityPlayer player) {
        var pos = targetTp.pos.clone();
        switch (targetTp.dir) {
            case 0: {// Coming out the top
                pos.y += -(targetTp.size.y / 2) - (player.size.y / 2) - TELEPORTER_BUFFER;
                break;
            }
            case 1: {// Coming out the right
                System.out.println(pos.x + (targetTp.size.x / 2) + (player.size.x / 2));
                pos.x += (targetTp.size.x / 2) + (player.size.x / 2) + TELEPORTER_BUFFER;
                break;
            }
            case 2: {// Coming out the bottom
                pos.y += (targetTp.size.y / 2) + (player.size.y / 2) + TELEPORTER_BUFFER;
                break;
            }
            case 3: {// Coming out the left
                pos.x += -(targetTp.size.x / 2) - (player.size.x / 2) - TELEPORTER_BUFFER;
                break;
            }
        }
        return pos;
    }

    public EntityTeleporter getTargetTeleporterOrLoad() {
        var tarWorld = this.getTargetWorldOrLoad();
        for (var entity : tarWorld.getEntities()) {
            if (!(entity instanceof EntityTeleporter tp))
                continue;

            if (tp.teleporterId == this.targetTeleporterId)
                return tp;
        }

        return null;
    }

    public World getTargetWorld() {
        return this.world.getRoom().getWorldById(this.targetWorldId, false);
    }

    public World getTargetWorldOrLoad() {
        return this.world.getRoom().getWorldById(this.targetWorldId, true);
    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        this.teleporterId = metadata.getJson().get("id").getAsInt();
        this.targetWorldId = metadata.getJson().get("targetWorld").getAsInt();
        this.targetTeleporterId = metadata.getJson().get("targetTp").getAsInt();
        this.dir = metadata.getJson().get("dir").getAsByte();
    }

    @Override
    public void writePacketEntityMetadata(ByteBuf buf) {
        buf.writeByte(this.dir);
    }

    @Override
    public String toString() {
        return "EntityTeleporter{" +
                "teleporterId=" + teleporterId +
                ", targetWorld=" + (this.getTargetWorld() == null ? "Not loaded yet or doesn't exist" : this.getTargetWorld().getWorldName()) +
                ", targetTeleporterId=" + targetTeleporterId +
                ", dir=" + dir +
                ", pos=" + pos +
                ", size=" + size +
                '}';
    }
}
