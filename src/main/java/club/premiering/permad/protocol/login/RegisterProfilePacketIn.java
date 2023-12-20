package club.premiering.permad.protocol.login;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;

public class RegisterProfilePacketIn extends BasePacket {
    public String username, password;

    @Override
    public void read() {
        this.username = NettyUtils.readString(data);
        this.password = NettyUtils.readString(data);
    }

    @Override
    public void write() {

    }
}
