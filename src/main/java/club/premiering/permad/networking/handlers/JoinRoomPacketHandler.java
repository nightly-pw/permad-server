package club.premiering.permad.networking.handlers;

import club.premiering.permad.PermaServer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.protocol.ui.JoinRoomPacketIn;

public class JoinRoomPacketHandler implements PacketHandler<JoinRoomPacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return false;
    }

    @Override
    public void handle(GameSession session, JoinRoomPacketIn packet) {
        PermaServer.getServer().getRoomManager().joinRoom(session, packet.getRoomName());
    }
}
