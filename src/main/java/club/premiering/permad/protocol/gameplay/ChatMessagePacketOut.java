package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatMessagePacketOut extends BasePacket {
    public String message;

    @Override
    public void read() {

    }

    @Override
    public void write() {
        NettyUtils.writeString(data, message);
    }
}
