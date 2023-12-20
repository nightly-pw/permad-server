package club.premiering.permad.protocol.ui;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.room.Room;
import club.premiering.permad.util.NettyUtils;

import java.util.Collection;

public class RoomListingPacketOut extends BasePacket {
    private Collection<Room> rooms;

    public RoomListingPacketOut(Collection<Room> rooms) {
        this.rooms = rooms;
    }
    
    @Override
    public void read() {
        
    }

    @Override
    public void write() {
        data.writeInt(this.rooms.size());
        for (var room : this.rooms) {
            NettyUtils.writeString(data, room.getRoomName());
            NettyUtils.writeString(data, room.getMetadata().name);
            NettyUtils.writeString(data, room.getMetadata().creator);
            data.writeInt(room.getSessions().size());
            data.writeBoolean(true);
        }
    }
}
