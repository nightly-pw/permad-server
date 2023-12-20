package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.format.EntityMetadata;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class EntityObstacle extends RigidEntity {
    @Getter
    @Setter
    private boolean usesCustomColor = false;
    @Getter
    @Setter
    private Color customColor;

    public EntityObstacle() {
        super();

        this.size = new Vector2(56, 200);
        this.setSolid(true);
    }

    public void setNoClip(boolean noClip) {
        this.setSolid(!noClip);
        this.setActive(!noClip);
    }

    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {

    }

    @Override
    public void onAddedToWorld() {

    }

    @Override
    public void doTick() {
        super.doTick();
    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        var noClipEl = metadata.getJson().get("noClip");
        this.setNoClip(noClipEl != null && noClipEl.getAsBoolean());
        var customColor = metadata.getJson().get("color");
        if (customColor != null) {
            this.usesCustomColor = true;
            this.customColor = new Color(customColor.getAsInt(), true);
        }
    }

    @Override
    public void writePacketEntityMetadata(ByteBuf buf) {
        super.writePacketEntityMetadata(buf);

        buf.writeBoolean(this.usesCustomColor);
        if (this.usesCustomColor)
            buf.writeInt(this.customColor.getRGB());
    }
}
