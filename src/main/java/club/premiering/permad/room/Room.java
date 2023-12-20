package club.premiering.permad.room;

import club.premiering.permad.entity.EntityPlayer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.world.World;
import club.premiering.permad.format.MapMetadata;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

//A room represents a Map, which is a collection of worlds to create a map
//This room stores many worlds, and is what players are directly joining
public interface Room {
    String getRoomName();
    MapMetadata getMetadata();
    Collection<GameSession> getSessions();
    void registerPlayer(GameSession session);
    void removePlayer(GameSession session);
    void switchPlayerWorld(EntityPlayer player, World newWorld);
    Collection<World> getActiveWorlds();
    World getWorldByName(String name, boolean shouldLoad);
    World getWorldById(int id, boolean shouldLoad);//Note: If should load is false, then the world will be null (indicating it doesn't exist) even if it does in the map
    World createWorld();
    void deleteWorld(World world);
    World getSpawnWorld();
    void broadcastPacket(BasePacket packet);
    void broadcastMessage(String message);
    ExecutorService getRoomWorker();//This will be used by all of the worlds
}
