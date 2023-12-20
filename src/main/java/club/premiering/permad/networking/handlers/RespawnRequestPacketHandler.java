package club.premiering.permad.networking.handlers;

import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.protocol.gameplay.RespawnRequestPacketIn;

public class RespawnRequestPacketHandler implements PacketHandler<RespawnRequestPacketIn> {
    @Override
    public boolean isSynchronousPacket() {
        return true;
    }

    @Override
    public void handle(GameSession session, RespawnRequestPacketIn packet) {
        if (session.room == null || session.world == null || session.player == null)
            return;

        var spawnWorld = session.world.getRoom().getSpawnWorld();
        session.room.switchPlayerWorld(session.player, spawnWorld);
        session.player.respawnPlayer();
        session.player.pos = spawnWorld.getWorldSpawn().clone();
        session.player.setBoostAmount(100f);
    }
}
