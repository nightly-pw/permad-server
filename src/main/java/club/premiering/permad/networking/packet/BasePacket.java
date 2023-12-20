package club.premiering.permad.networking.packet;

import io.netty.buffer.ByteBuf;

// Manages packets that are sent between the server and the client
public abstract class BasePacket {
    public int packetId;
    public ByteBuf data;

    public abstract void read();
    public abstract void write();
}
