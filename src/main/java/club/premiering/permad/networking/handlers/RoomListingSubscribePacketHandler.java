package club.premiering.permad.networking.handlers;

import club.premiering.permad.PermaServer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.protocol.ui.RoomListingSubscribePacketIn;

public class RoomListingSubscribePacketHandler implements PacketHandler<RoomListingSubscribePacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return false;
    }

    @Override
    public void handle(GameSession session, RoomListingSubscribePacketIn packet) {
        PermaServer.getServer().getRoomManager().subscribe(session);
    }
}
