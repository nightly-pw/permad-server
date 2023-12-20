package club.premiering.permad.room;

import club.premiering.permad.PermaConfig;
import club.premiering.permad.PermaGlobalMap;
import club.premiering.permad.PermaGlobals;
import club.premiering.permad.PermaServer;
import club.premiering.permad.networking.GameSession;
import club.premiering.permad.protocol.ui.RoomListingPacketOut;
import club.premiering.permad.util.FileUtils;
import club.premiering.permad.util.InstanceRegistry;
import club.premiering.permad.format.MapData;
import club.premiering.permad.format.v2.V2MapData;
import club.premiering.permad.format.v2.V2MapDataSkapConverter;

import java.util.*;

public class RoomManagerImpl implements RoomManager {
    private static final InstanceRegistry<String, MapData> MAP_DATA_REGISTRY = new InstanceRegistry<>();

    static {
        MAP_DATA_REGISTRY.register("v2", V2MapData.class);
        MAP_DATA_REGISTRY.register("sk-v2", V2MapDataSkapConverter.class);
    }

    private List<Room> availableRooms = new ArrayList<>();
    private Collection<GameSession> subscribedSessions = new HashSet<>();

    public RoomManagerImpl() throws Exception {
        for (PermaGlobalMap globalMap : PermaGlobals.CONFIG.globalMaps) {
            this.createRoom(new RoomData(globalMap.displayName, globalMap.format, FileUtils.loadFileData(globalMap.path)));
        }
    }
    
    @Override
    public Room createRoom(GameSession session, RoomData roomData) throws Exception {
        var room = this.createRoom(roomData);
        if (session.room != null)
            session.room.removePlayer(session);

        room.registerPlayer(session);

        return room;
    }

    @Override
    public Room createRoom(RoomData roomData) throws Exception {
        var format = MAP_DATA_REGISTRY.get(roomData.mapFormat);
        if (format == null)
            throw new IllegalArgumentException("Map format " + roomData.mapFormat + " does not exist!");
        var room = new RoomImpl(roomData.roomName, MAP_DATA_REGISTRY.create(roomData.mapFormat), roomData.mapData);
        this.updateSubscribers();
        this.availableRooms.add(room);
        return room;
    }

    @Override
    public void deleteRoom(Room room) {
        this.availableRooms.remove(room);
        this.updateSubscribers();
    }

    @Override
    public void joinRoom(GameSession session, String roomName) {
        for (Iterator<Room> it = this.availableRooms.iterator(); it.hasNext(); ) {
            var room = it.next();
            if (room.getRoomName().equals(roomName)) {
                if (session.room != null) {
                    session.room.removePlayer(session);
                }
                room.registerPlayer(session);
                return;
            }
        }
    }

    @Override
    public void subscribe(GameSession session) {
        this.subscribedSessions.add(session);
        this.updateSubscriber(session);
    }

    @Override
    public void unsubscribe(GameSession session) {
        this.subscribedSessions.remove(session);
    }

    @Override
    public Collection<Room> getRooms() {
        return this.availableRooms;
    }

    private void updateSubscriber(GameSession session) {
        var packet = new RoomListingPacketOut(this.availableRooms);
        PermaServer.getServer().getNetworkServer().sendAsync(session, packet);
    }

    private void updateSubscribers() {
        var packet = new RoomListingPacketOut(this.availableRooms);
        for (Iterator<GameSession> it = this.subscribedSessions.iterator(); it.hasNext(); ) {
            var session = it.next();
            if (session.canSend()) {
                PermaServer.getServer().getNetworkServer().sendAsync(session, packet);
            } else {
                this.subscribedSessions.remove(session);
            }
        }
    }
}
