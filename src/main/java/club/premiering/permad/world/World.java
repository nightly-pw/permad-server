package club.premiering.permad.world;

import club.premiering.permad.entity.Entity;
import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.math.Vector2;
import club.premiering.permad.math.Vector4;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.networking.packet.PacketQueue;
import club.premiering.permad.room.Room;
import club.premiering.permad.tick.TickScheduler;
import club.premiering.permad.tick.Tickable;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

public interface World extends Tickable {
    void start();
    void end();
    void doTick();
    Room getRoom();
    int getWorldId();
    PacketQueue getIncomingPacketQueue();
    PacketQueue getOutgoingPacketQueue();
    void submitPacket(GameSession session, BasePacket packet);
    void broadcastPacket(BasePacket packet);
    TickScheduler getTickScheduler();
    /*
     * This should be used when the World API is being called asynchronously
     */
    <T> void runNextTick(Runnable runnable);
    String getWorldName();
    void setWorldName(String worldName);
    Vector4 getWorldSize();
    void setWorldSize(Vector4 size);
    Vector2 getWorldSpawn();
    void setWorldSpawn(Vector2 spawn);
    void addEntity(Entity entity);
    void removeEntity(Entity entity);
    void handleJoin(GameSession session);
    void handleLeave(GameSession session);
    void handleWorldSwitchJoin(EntityPlayer player);
    void handleWorldSwitchLeave(EntityPlayer player);
    ExecutorService getWorker();
    Collection<Entity> getEntities();
    Collection<EntityPlayer> getPlayers();
    int getPlayerCount();
}
