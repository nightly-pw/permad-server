package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.networking.packet.BasePacket;

public class LocalPlayerStatePacketOut extends BasePacket {
    public EntityPlayer player;

    public LocalPlayerStatePacketOut(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        data.writeFloat(player.getBoostAmount());
    }
}
