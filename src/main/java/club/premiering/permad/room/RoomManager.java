package club.premiering.permad.room;

import club.premiering.permad.networking.GameSession;

import java.util.Collection;

public interface RoomManager {
    Room createRoom(GameSession session, RoomData roomData) throws Exception;
    Room createRoom(RoomData roomData) throws Exception;
    void deleteRoom(Room room);
    void joinRoom(GameSession session, String roomName);
    /*
     * Calling would subscribe the session to updates on room listings
     */
    void subscribe(GameSession session);
    void unsubscribe(GameSession session);
    Collection<Room> getRooms();
}
