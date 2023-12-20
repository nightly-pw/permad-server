package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.Entity;

public class EntityMetadataUpdatePacketOut extends BaseEntityPacketOut {
    public EntityMetadataUpdatePacketOut(Entity entity) {
        super(entity);
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        super.write();
        this.entity.writePacketEntityMetadata(data);
    }
}
