package club.premiering.permad.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class NettyUtils {
    public static void writeString(ByteBuf buf, String s) {
        buf.writeInt(s.length());
        buf.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String readString(ByteBuf buf) {
        var len = buf.readInt();
        byte[] buffer = new byte[len];
        buf.readBytes(buffer);
        return new String(buffer);
    }
}
