package club.premiering.permad.protocol.login;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.util.NettyUtils;

public class LoginProfilePacketIn extends BasePacket {
    public String username, password;

    @Override
    public void read() {
        //Keep in mind in production we should have WSS/encryption.
        this.username = NettyUtils.readString(data);
        this.password = NettyUtils.readString(data);
    }

    @Override
    public void write() {

    }
}
