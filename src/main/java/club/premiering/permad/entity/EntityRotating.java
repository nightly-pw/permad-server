package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.format.EntityMetadata;

public class EntityRotating extends Entity {
    public int entityType;
    public float degPerSecond = 90f;
    public float startDeg = 0f;
    public Vector2 rotationPoint = new Vector2(0, 0);

    private Entity childEntity;

    @Override
    public void onAddedToWorld() {
        this.childEntity = EntityRegistry.ENTITY_REGISTRY.create(this.entityType);
        this.childEntity.size = this.size.clone();
        this.rot = startDeg;
        this.childEntity.rot = startDeg;
        this.world.addEntity(this.childEntity);
    }

    @Override
    public void doTick() {
        this.rot += this.degPerSecond / 60f;
        if (this.rot > 360) {
            this.rot = this.rot - 360;
        } else if (this.rot < 0) {
            this.rot = this.rot + 360;
        }

        this.childEntity.rot = this.rot;
        this.childEntity.pos = this.pos.clone().rotate((float) Math.toRadians(this.rot), this.rotationPoint);
    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        super.readSerializedEntityMetadata(metadata);

        this.entityType = metadata.getJson().get("entType").getAsInt();
        this.rotationPoint = metadata.getVector2(metadata.getJson().getAsJsonObject("rotPnt"));
        this.degPerSecond = metadata.getJson().get("degPs").getAsFloat();
        this.startDeg = metadata.getJson().get("startDeg").getAsFloat();
    }
}
