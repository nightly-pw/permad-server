package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;

public class ChatMessagePacketIn extends BasePacket {
    public String message;

    @Override
    public void read() {
        this.message = NettyUtils.readString(data);
    }

    @Override
    public void write() {

    }
}
