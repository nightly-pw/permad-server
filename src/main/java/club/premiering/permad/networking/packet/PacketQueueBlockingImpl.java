package club.premiering.permad.networking.packet;

import club.premiering.permad.networking.GameSession;
import com.google.common.collect.Queues;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//This implementation is a blocking queue
public class PacketQueueBlockingImpl implements PacketQueue {
    // TODO: 9/5/2023 Was previously leaking memory, maybe fixed after I switched the queue type
    private final ConcurrentLinkedQueue<QueuedPacket> queue = Queues.newConcurrentLinkedQueue();

    @Override
    public void enqueue(GameSession session, BasePacket packet) {
        queue.add(new QueuedPacket(session, packet));
    }

    @Override
    public synchronized Queue<QueuedPacket> fetchQueue() {
        Queue<QueuedPacket> packets = Queues.newConcurrentLinkedQueue();
        //Poll what we can
        var n = queue.size();
        for (var i = 0; i < n; i++) {
            packets.add(queue.poll());
        }
        return packets;
    }
}
