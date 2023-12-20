package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.Entity;
import club.premiering.permad.entity.EntityRegistry;

public class SpawnEntityPacketOut extends BaseEntityPacketOut {
    public SpawnEntityPacketOut(Entity entity) {
        super(entity);
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        super.write();

        data.writeInt(EntityRegistry.ENTITY_REGISTRY.get(entity.getClass()));
        this.entity.writePacketEntityData(this.data);
        this.entity.writePacketEntityMetadata(this.data);
    }
}
