package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.Entity;

public class DestroyEntityPacketOut extends BaseEntityPacketOut {
    public DestroyEntityPacketOut(Entity entity) {
        super(entity);
    }

    @Override
    public void read() {

    }
}
