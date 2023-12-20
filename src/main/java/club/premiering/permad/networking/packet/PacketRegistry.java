package club.premiering.permad.networking.packet;

import club.premiering.permad.networking.handlers.*;
import club.premiering.permad.protocol.gameplay.*;
import club.premiering.permad.protocol.login.LoginProfilePacketIn;
import club.premiering.permad.protocol.login.RegisterProfilePacketIn;
import club.premiering.permad.protocol.ui.JoinRoomPacketIn;
import club.premiering.permad.protocol.ui.RoomListingPacketOut;
import club.premiering.permad.protocol.ui.RoomListingSubscribePacketIn;
import club.premiering.permad.util.InstanceRegistry;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;

// Contains the mapping of all packets used to communicate with the clients.
public class PacketRegistry {
    public static final InstanceRegistry<Integer, BasePacket> PACKET_REGISTRY = new InstanceRegistry<>();
    public static final InstanceRegistry<Integer, PacketHandler<?>> HANDLER_REGISTRY = new InstanceRegistry<>();

    static {
        PACKET_REGISTRY.register(1, MoveRequestPacketIn.class);
        HANDLER_REGISTRY.register(1, MoveRequestPacketHandler.class);
        PACKET_REGISTRY.register(2, SpawnEntityPacketOut.class);
        PACKET_REGISTRY.register(3, EntityPositionPacketOut.class);
        PACKET_REGISTRY.register(4, WorldInfoPacketOut.class);
        PACKET_REGISTRY.register(5, DestroyEntityPacketOut.class);
        PACKET_REGISTRY.register(6, LocalPlayerStatePacketOut.class);
        PACKET_REGISTRY.register(7, EntityMetadataUpdatePacketOut.class);
        PACKET_REGISTRY.register(8, ChatMessagePacketIn.class);
        HANDLER_REGISTRY.register(8, ChatMessagePacketHandler.class);
        PACKET_REGISTRY.register(9, ChatMessagePacketOut.class);
        PACKET_REGISTRY.register(10, RespawnRequestPacketIn.class);
        HANDLER_REGISTRY.register(10, RespawnRequestPacketHandler.class);
        PACKET_REGISTRY.register(11, RoomPlayerListPacketOut.class);
        PACKET_REGISTRY.register(12, RoomListingPacketOut.class);
        PACKET_REGISTRY.register(13, RoomListingSubscribePacketIn.class);
        HANDLER_REGISTRY.register(13, RoomListingSubscribePacketHandler.class);
        PACKET_REGISTRY.register(14, JoinRoomPacketIn.class);
        HANDLER_REGISTRY.register(14, JoinRoomPacketHandler.class);
        PACKET_REGISTRY.register(15, LoginProfilePacketIn.class);
        HANDLER_REGISTRY.register(15, LoginProfilePacketHandler.class);
        PACKET_REGISTRY.register(16, RegisterProfilePacketIn.class);
        HANDLER_REGISTRY.register(16, RegisterProfilePacketHandler.class);
        PACKET_REGISTRY.register(99, TestBBPacketOut.class);
    }

    public static <T extends BasePacket> T createPacket(int i, ByteBuf buf, Class<T> pClass) {
        try {
            var packet = pClass.getConstructor(null).newInstance();
            packet.data = buf;
            packet.read();
            return packet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get constructor for packet " + i + "!");
        }
    }
}
