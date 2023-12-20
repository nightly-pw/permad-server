package club.premiering.permad.networking.handlers;

import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.protocol.gameplay.MoveRequestPacketIn;

public class MoveRequestPacketHandler implements PacketHandler<MoveRequestPacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return true;
    }

    @Override
    public void handle(GameSession session, MoveRequestPacketIn packet) {
        var player = session.player;
        if (player != null) {
            if (packet.pressed)
                player.addDirectionState(packet.movementDir);
            else
                player.removeDirectionState(packet.movementDir);
        }
    }
}
