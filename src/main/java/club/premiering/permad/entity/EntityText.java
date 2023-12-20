package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.util.NettyUtils;
import club.premiering.permad.format.EntityMetadata;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class EntityText extends Entity {
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private TextAlignment alignment;
    @Getter
    @Setter
    private Color color;

    public EntityText() {
        this.size = new Vector2(20, 20);
    }

    @Override
    public void doTick() {

    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        this.text = metadata.getJson().get("text").getAsString();
        this.color = new Color(metadata.getJson().get("color").getAsInt());
        this.alignment = TextAlignment.fromByte(metadata.getJson().get("alignment").getAsByte());
    }

    @Override
    public void writePacketEntityMetadata(ByteBuf buf) {
        NettyUtils.writeString(buf, this.text);
        buf.writeInt(this.color.getRGB());
        buf.writeByte(this.alignment.toByte());
    }
}
