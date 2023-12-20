package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;
import club.premiering.permad.world.World;

public class WorldInfoPacketOut extends BasePacket {
    public EntityPlayer recipient;
    public World world;

    public WorldInfoPacketOut(EntityPlayer recipient, World world) {
        this.recipient = recipient;
        this.world = world;
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        NettyUtils.writeString(data, world.getWorldName());
        data.writeFloat(world.getWorldSize().x);
        data.writeFloat(world.getWorldSize().y);
        data.writeFloat(world.getWorldSize().z);
        data.writeFloat(world.getWorldSize().w);
        data.writeInt(recipient.entityId.getId());
    }
}
