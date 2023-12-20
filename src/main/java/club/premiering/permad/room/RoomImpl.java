package club.premiering.permad.room;

import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.protocol.gameplay.ChatMessagePacketOut;
import club.premiering.permad.protocol.gameplay.RoomPlayerListPacketOut;
import club.premiering.permad.world.WorldImpl;
import club.premiering.permad.format.MapData;
import club.premiering.permad.world.World;
import club.premiering.permad.format.MapMetadata;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomImpl implements Room {
    private Collection<GameSession> sessions = new CopyOnWriteArrayList<>();
    private Collection<World> activeWorlds = new CopyOnWriteArrayList<>();

    private final String roomName;

    private final MapData data;
    private final MapMetadata mapMetadata;

    private World spawnWorld;

    private ExecutorService roomWorker = Executors.newFixedThreadPool(1);

    public RoomImpl(String roomName, MapData format, byte[] map) throws Exception {
        this.roomName = roomName;
        this.data = format;
        format.loadMapData(map);
        this.mapMetadata = format.getMetadata();
        this.spawnWorld = this.createWorld();
        this.spawnWorld.start();
        format.loadWorld(format.getSpawnWorldId(), this.spawnWorld);
    }

    @Override
    public String getRoomName() {
        return this.roomName;
    }

    @Override
    public MapMetadata getMetadata() {
        return this.mapMetadata;
    }

    @Override
    public Collection<GameSession> getSessions() {
        return sessions;
    }

    @Override
    public void registerPlayer(GameSession session) {
        session.room = this;
        spawnWorld.handleJoin(session);
        this.sessions.add(session);

        this.updatePlayerList();

        session.player.pos = session.world.getWorldSpawn().clone();
    }

    @Override
    public void removePlayer(GameSession session) {
        session.room = null;
        session.world.handleLeave(session);
        this.sessions.remove(session);

        this.updatePlayerList();
    }

    @Override
    public void switchPlayerWorld(EntityPlayer player, World newWorld) {
        if (player.world != null) {
            player.world.handleWorldSwitchLeave(player);
            if (player.world.getPlayerCount() == 0 && player.world != this.spawnWorld) {
                this.deleteWorld(player.world);
            }
        }

        newWorld.handleWorldSwitchJoin(player);

        this.updatePlayerList();
    }

    @Override
    public Collection<World> getActiveWorlds() {
        return this.activeWorlds;
    }

    @Override
    public World getWorldByName(String name, boolean shouldLoad) {
        if (!shouldLoad) {
            for (World world : this.activeWorlds) {
                if (world.getWorldName().equalsIgnoreCase(name))
                    return world;
            }
            return null;
        }
        try {
            var id = data.getWorldIdFromName(name);
            return this.getWorldById(id, true);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public World getWorldById(int id, boolean shouldLoad) {
        for (World world : this.activeWorlds) {
            if (world.getWorldId() == id)
                return world;
        }

        if (!shouldLoad)
            return null;

        //If it exists in the map then let's create it
        var world = this.createWorld();
        var created = this.data.loadWorld(id, world);
        if (created) {
            world.start();
            return world;
        }

        this.deleteWorld(world);

        return null;
    }

    @Override
    public World createWorld() {
        var world = new WorldImpl(this, -1);
        this.activeWorlds.add(world);
        return world;
    }

    @Override
    public void deleteWorld(World world) {
        world.end();
        this.activeWorlds.remove(world);
    }

    @Override
    public World getSpawnWorld() {
        return this.spawnWorld;
    }

    @Override
    public void broadcastPacket(BasePacket packet) {
        for (var world : this.activeWorlds) {
            world.broadcastPacket(packet);
        }
    }

    @Override
    public void broadcastMessage(String message) {
        var pckt = new ChatMessagePacketOut(message);
        this.broadcastPacket(pckt);
    }

    @Override
    public ExecutorService getRoomWorker() {
        return this.roomWorker;
    }

    protected void updatePlayerList() {
        this.broadcastPacket(new RoomPlayerListPacketOut(this));
    }
}
