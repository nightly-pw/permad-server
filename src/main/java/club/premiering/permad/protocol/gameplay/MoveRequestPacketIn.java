package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.networking.packet.BasePacket;

public class MoveRequestPacketIn extends BasePacket {
    public MoveDirection movementDir;
    public boolean pressed;

    @Override
    public void read() {
        this.movementDir = MoveDirection.getFromByte(data.readByte());
        this.pressed = data.readBoolean();
    }

    @Override
    public void write() {

    }
}
