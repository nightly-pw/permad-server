package club.premiering.permad.world;

import club.premiering.permad.PermaGlobals;
import club.premiering.permad.PermaServer;
import club.premiering.permad.entity.Entity;
import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.math.Vector2;
import club.premiering.permad.math.Vector4;
import club.premiering.permad.networking.NetworkManager;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.*;
import club.premiering.permad.protocol.gameplay.*;
import club.premiering.permad.room.Room;
import club.premiering.permad.tick.TickScheduler;
import com.google.common.collect.Queues;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class WorldImpl implements World {
    private NetworkManager networkManager = PermaServer.getServer().getNetworkServer();

    @Getter
    private Room room;
    @Getter
    private int worldId;

    @Getter
    private TickScheduler tickScheduler;
    @Getter
    private PacketQueue incomingPacketQueue;
    @Getter
    private PacketQueue outgoingPacketQueue;

    @Getter
    @Setter
    private Vector4 worldSize = new Vector4(-512, -512, 512, 512);
    @Getter
    @Setter
    private Vector2 worldSpawn = new Vector2(0, 0);
    @Getter
    @Setter
    private String worldName = "Unnamed world";

    private Collection<Entity> entities = new CopyOnWriteArrayList<>();
    private Queue<Runnable> scheduledActions = Queues.newConcurrentLinkedQueue();

    public WorldImpl(Room room, int worldId) {
        this.room = room;
        this.worldId = worldId;

        this.tickScheduler = new TickScheduler(this);
        this.incomingPacketQueue = new PacketQueueBlockingImpl();
        this.outgoingPacketQueue = new PacketQueueBlockingImpl();
    }

    @Override
    public void start() {
        this.tickScheduler.startTicking();
    }

    @Override
    public void end() {
        this.tickScheduler.stopTicking();
    }

    @Override
    public void doTick() {
        for (int i = 0; i < this.scheduledActions.size(); i++) {
            var action = this.scheduledActions.poll();
            try {
                action.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        var packetQueue = getIncomingPacketQueue().fetchQueue();
        for (QueuedPacket packet : packetQueue) {
            var handler = this.networkManager.getHandler(packet.packet);
            this.handlePacket(handler, packet);
        }

        for (var entity : this.entities) {
            //entity.lastRot = entity.rot;
            entity.doTick();

            this.broadcastPacket(new EntityPositionPacketOut(entity));
            entity.lastPos = entity.pos.clone();
            entity.lastSize = entity.size.clone();
            entity.lastRot = entity.rot;
        }

        // Process outgoing packets
        this.networkManager.sendAsyncQueue(this.outgoingPacketQueue);
    }

    @Override
    public void submitPacket(GameSession session, BasePacket packet) {
        this.outgoingPacketQueue.enqueue(session, packet);
    }

    @Override
    public void broadcastPacket(BasePacket packet) {
        for (Entity entity : this.entities) {
            if (entity instanceof EntityPlayer) {
                var player = (EntityPlayer) entity;
                submitPacket(player.session, packet);
            }
        }
    }

    @Override
    public <T> void runNextTick(Runnable runnable) {
        this.scheduledActions.add(runnable);
    }

    private <T extends BasePacket> void handlePacket(PacketHandler<T> handler, QueuedPacket packet) {
        handler.handle(packet.recv, (T) packet.packet);
    }

    @Override
    public void addEntity(Entity entity) {
        entity.world = this;
        this.entities.add(entity);

        if (PermaGlobals.CONFIG.debugMode)
            System.out.println("Added entity " + entity);

        entity.onAddedToWorld();

        this.broadcastPacket(new SpawnEntityPacketOut(entity));
    }

    @Override
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);

        if (PermaGlobals.CONFIG.debugMode)
            System.out.println("Removed entity " + entity);

        entity.onRemovedFromWorld();

        this.broadcastPacket(new DestroyEntityPacketOut(entity));
    }

    @Override
    public void handleJoin(GameSession session) {
        var player = new EntityPlayer(session);

        this.handleJoinPlayer(player);
    }

    private void handleJoinPlayer(EntityPlayer player) {
        this.submitPacket(player.session, new WorldInfoPacketOut(player, this));
        this.submitPacket(player.session, new LocalPlayerStatePacketOut(player));

        addEntity(player);
        player.session.setState(player);

        var iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity == player)
                continue;

            this.submitPacket(player.session, new SpawnEntityPacketOut(entity));
        }
    }

    @Override
    public void handleLeave(GameSession session) {
        var player = session.player;
        player.world.removeEntity(player);
    }

    @Override
    public void handleWorldSwitchJoin(EntityPlayer player) {
        player.setOnGround(false);
        this.handleJoinPlayer(player);
    }

    @Override
    public void handleWorldSwitchLeave(EntityPlayer player) {
        this.handleLeave(player.session);
    }

    @Override
    public ExecutorService getWorker() {
        return this.room.getRoomWorker();
    }

    @Override
    public Collection<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public Collection<EntityPlayer> getPlayers() {
        Collection<EntityPlayer> players = new ArrayList<>();
        for (var ent : this.entities) {
            if (ent instanceof EntityPlayer player)
                players.add(player);
        }
        return players;
    }

    @Override
    public int getPlayerCount() {
        int players = 0;
        for (var ent : this.entities) {
            if (ent instanceof EntityPlayer)
                players++;
        }
        return players;
    }
}
