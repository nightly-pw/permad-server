package club.premiering.permad.networking.handlers;

import club.premiering.permad.PermaServer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.protocol.gameplay.ChatMessagePacketIn;

public class ChatMessagePacketHandler implements PacketHandler<ChatMessagePacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return false;
    }

    @Override
    public void handle(GameSession session, ChatMessagePacketIn packet) {
        if (session.room == null || session.player == null)
            return;

        if (packet.message.startsWith("/") && !packet.message.startsWith("/ ")) {
            PermaServer.getServer().getCommandManager().handleCommand(session, packet.message);
            return;
        }
        session.room.broadcastMessage(session.player.getName() + ": " + packet.message);
    }
}
