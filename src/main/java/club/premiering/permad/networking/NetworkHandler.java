package club.premiering.permad.networking;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.networking.packet.PacketHandler;
import club.premiering.permad.networking.packet.PacketQueue;

// Manages the websocket server and packets
public interface NetworkHandler {
    void start();
    void stop();
    void onConnect(GameSession session);
    void onPacket(GameSession session, byte[] data);
    void onDisconnect(GameSession session);
    PacketHandler<?> getHandler(BasePacket packet);
    void sendAsyncQueue(PacketQueue packets);
    void sendAsync(GameSession session, BasePacket packet);
    void sendAsync(GameSession session, byte[] data);
}
