package club.premiering.permad.protocol.ui;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;
import lombok.Getter;

public class JoinRoomPacketIn extends BasePacket {
    @Getter
    private String roomName;

    @Override
    public void read() {
        this.roomName = NettyUtils.readString(data);
    }

    @Override
    public void write() {

    }
}
