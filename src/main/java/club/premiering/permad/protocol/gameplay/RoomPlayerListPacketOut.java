package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.networking.packet.BasePacket;
import club.premiering.permad.room.Room;
import club.premiering.permad.util.NettyUtils;

public class RoomPlayerListPacketOut extends BasePacket {
    public Room room;

    public RoomPlayerListPacketOut(Room room) {
        this.room = room;
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {
        var totalPlayers = this.room.getSessions().size();
        data.writeInt(totalPlayers);
        for (var sess : this.room.getSessions()) {
            var player = sess.player;
            NettyUtils.writeString(data, player.getName());
            data.writeInt(player.getColor().getRGB());
            NettyUtils.writeString(data, player.world.getWorldName());
        }
    }
}
