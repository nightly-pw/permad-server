package club.premiering.permad.networking.packet;

import club.premiering.permad.networking.GameSession;

// Used to handle incoming packets
public interface PacketHandler<T extends BasePacket> {
    boolean isSynchronousPacket();//If the packet is synchronous, it will be handled in the next available tick of the sending player.
    void handle(GameSession session, T packet);
}
