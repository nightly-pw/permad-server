package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.protocol.gameplay.EntityMetadataUpdatePacketOut;
import club.premiering.permad.tick.Tickable;
import club.premiering.permad.world.World;
import club.premiering.permad.format.EntityMetadata;
import io.netty.buffer.ByteBuf;

public abstract class Entity implements Tickable {
    public World world;
    public Vector2 pos = new Vector2(0, 0);//Center pos
    public Vector2 lastPos = new Vector2(0, 0);//Not sent over by server
    public Vector2 lastSize = new Vector2(0, 0);
    public Vector2 size = new Vector2(0, 0);
    public float rot = 0f;
    public float lastRot = 0f;
    public EntityId entityId = EntityId.createId();

    public void onAddedToWorld() {}
    public void onRemovedFromWorld() {}

    //This is used for deserializing the data of a stored entity
    public final void readSerializedEntityData(Vector2 pos, Vector2 size, float rot) {
        this.pos = pos.clone();
        this.size = size.clone();
        this.rot = rot;
    }

    public final void writePacketEntityData(ByteBuf buf) {
        buf.writeFloat(this.pos.x);
        buf.writeFloat(this.pos.y);
        buf.writeFloat(this.rot);
        buf.writeFloat(this.size.x);
        buf.writeFloat(this.size.y);
    }

    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        //Nothing..., to be overwritten
    }

    public void writePacketEntityMetadata(ByteBuf buf) {
        //Nothing..., to be overwritten
    }

    public void broadcastMetadataUpdate() {
        this.world.broadcastPacket(new EntityMetadataUpdatePacketOut(this));
    }
}
