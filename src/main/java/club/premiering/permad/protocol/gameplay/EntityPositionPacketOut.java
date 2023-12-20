package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.Entity;

public class EntityPositionPacketOut extends BaseEntityPacketOut {
    public boolean shouldInterpolate = true;

    public EntityPositionPacketOut(Entity entity) {
        this(entity, true);
    }

    public EntityPositionPacketOut(Entity entity, boolean shouldInterpolate) {
        super(entity);

        this.shouldInterpolate = shouldInterpolate;
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        super.write();

        data.writeFloat(entity.pos.x);
        data.writeFloat(entity.pos.y);
        data.writeFloat(entity.rot);
    }

    @Override
    public String toString() {
        return "EntityPositionPacketOut{" +
                "shouldInterpolate=" + shouldInterpolate +
                '}';
    }
}
