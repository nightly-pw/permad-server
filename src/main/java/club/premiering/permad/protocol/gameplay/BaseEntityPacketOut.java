package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.Entity;
import club.premiering.permad.networking.packet.BasePacket;

public abstract class BaseEntityPacketOut extends BasePacket {
    public Entity entity;

    public BaseEntityPacketOut(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void write() {
        data.writeInt(entity.entityId.getId());
    }
}
