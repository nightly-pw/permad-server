package club.premiering.permad.networking.packet;

import club.premiering.permad.networking.GameSession;

import java.util.Queue;

// A queue of packets that were received during a world tick
// These are to be processed in the next available tick of the world housing the queue.
public interface PacketQueue {
    void enqueue(GameSession session, BasePacket packet);

    Queue<QueuedPacket> fetchQueue();
}
