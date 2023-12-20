package club.premiering.permad.networking.packet;

import club.premiering.permad.networking.GameSession;

// A packet which is waiting in a queue to be sent out from the server to a client.
public class QueuedPacket {
    public GameSession recv;
    public BasePacket packet;

    public QueuedPacket(GameSession recv, BasePacket packet) {
        this.recv = recv;
        this.packet = packet;
    }
}
