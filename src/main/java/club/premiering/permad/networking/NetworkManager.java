package club.premiering.permad.networking;

import club.premiering.permad.PermaGlobals;
import club.premiering.permad.networking.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Implements the management of the websocket server and packets
public class NetworkManager implements NetworkHandler {
    private GameWebsocket websocket;

    private Map<Integer, PacketHandler<?>> packetHandlers = new HashMap<>();

    // TODO: 10/19/2023 Fix config and add
    private ExecutorService packetSenderPool = Executors.newFixedThreadPool(6);//A bit too much...?

    public NetworkManager() throws Exception {
        websocket = new GameWebsocket(this, PermaGlobals.WS_PORT);

        for (var entry : PacketRegistry.HANDLER_REGISTRY.registry.entrySet()) {
            this.packetHandlers.put(entry.getKey(), this.createHandler(entry.getValue()));
        }
    }

    public void start() {
        websocket.start();
    }

    @Override
    public void stop() {
        try {
            websocket.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Lots of generics...
    private PacketHandler<? extends BasePacket> createHandler(Class<? extends PacketHandler<? extends BasePacket>> packetClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return packetClass.getConstructor(new Class[]{}).newInstance();
    }

    @SneakyThrows
    @Override
    public void onConnect(GameSession session) {
        if (PermaGlobals.CONFIG.debugMode)
            System.out.println("Connected " + session.handle);
    }

    @Override
    public void onPacket(GameSession session, byte[] data) {
        var buf = Unpooled.wrappedBuffer(data);
        int pid = buf.readInt();
        var pClass = PacketRegistry.PACKET_REGISTRY.get(pid);
        if (pClass == null) {
            return;//drop
        }

        var packet = PacketRegistry.createPacket(pid, buf, pClass);
        packet.packetId = pid;
        PacketHandler<? extends BasePacket> handler = this.packetHandlers.get(pid);
        this.handlePacket(session, handler, packet);
    }

    private <T extends BasePacket> void handlePacket(GameSession session, PacketHandler<T> handler, BasePacket packet) {
        if (!handler.isSynchronousPacket()) {
            handler.handle(session, (T) packet);
        } else {
            if (session.world == null)
                return;

            session.world.getIncomingPacketQueue().enqueue(session, packet);
        }
    }

    @Override
    public void onDisconnect(GameSession session) {
        if (PermaGlobals.CONFIG.debugMode)
            System.out.println("Disconnected " + session.handle);
        if (session.room != null)
            session.room.removePlayer(session);
    }

    @Override
    public PacketHandler<?> getHandler(BasePacket packet) {
        return this.packetHandlers.get(packet.packetId);
    }

    @Override
    public void sendAsyncQueue(PacketQueue queue) {
        this.packetSenderPool.submit(() -> {
            Map<GameSession, BulkNetworkPacket> outgoingPackets = new HashMap<>();

            //A single network packet that is sent to the client contains many different "sub packets", or deltas of the game
            //Firstly, we will create these packets
            Collection<QueuedPacket> packets = queue.fetchQueue();
            for (var queued : packets) {
                var packet = outgoingPackets.get(queued.recv);
                if (packet == null) {
                    packet = new BulkNetworkPacket(queued.recv);
                    outgoingPackets.put(queued.recv, packet);
                }
                queued.packet.data = packet.data;

                queued.packet.data.writeInt(PacketRegistry.PACKET_REGISTRY.get(queued.packet.getClass()));
                queued.packet.write();
                packet.packetCount++;
            }

            //Then, we will send these packets out everyone, nicely bundled in one network packet
            for (var pair : outgoingPackets.entrySet()) {
                var data = pair.getValue().data;
                data.setIndex(0, 0);
                data.writeBoolean(true);//This is a bulk packet
                data.writeInt(pair.getValue().packetCount);
                pair.getKey().handle.send(data.array());
            }
        });
    }

    @Override
    public void sendAsync(GameSession session, BasePacket packet) {
        this.packetSenderPool.submit(() -> {
           packet.data = Unpooled.buffer();
           packet.data.writeBoolean(false);
           packet.data.writeInt(PacketRegistry.PACKET_REGISTRY.get(packet.getClass()));
           packet.write();
           session.handle.send(packet.data.array());
        });
    }

    @Override
    public void sendAsync(GameSession session, byte[] data) {
        this.packetSenderPool.submit(() -> {
            session.handle.send(data);
        });
    }

    static class BulkNetworkPacket {
        public GameSession recv;
        public ByteBuf data = Unpooled.buffer();
        public int packetCount = 0;

        public BulkNetworkPacket(GameSession session) {
            this.recv = session;

            //We reserve 5 bytes of space because we write whether it's a bulk packet (yes), then the packet count
            data.setIndex(5, 5);
        }
    }
}
