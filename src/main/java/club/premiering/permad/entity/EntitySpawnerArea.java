package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.math.Vector4;
import club.premiering.permad.format.EntityMetadata;

import java.util.Collection;
import java.util.HashSet;

//This entity represents an area where specific entities are spawned and move around in this area
public class EntitySpawnerArea extends RigidEntity {
    public int spawnType;
    public int spawnCount;
    public float entityHalfSize;

    private Collection<SpawnerEntity> entities = new HashSet<>();

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        //Add our entities
        for (int i = 0; i < this.spawnCount; i++) {
            var ent = (SpawnerEntity) EntityRegistry.ENTITY_REGISTRY.create(this.spawnType);
            ent.setSpawnerArea(this);
            ent.size = new Vector2(entityHalfSize * 2, entityHalfSize * 2);
            ent.pos = this.getRandomPosition(ent.size);
            this.world.addEntity(ent);
        }
    }

    @Override
    public void doTick() {
        super.doTick();
    }

    public void onChildRemoved(SpawnerEntity entity) {
        this.entities.remove(entity);
    }

    protected Vector2 getRandomPosition(Vector2 size) {
        var bounds = this.getBounds(size);
        return new Vector2(randomFloat(bounds.x, bounds.z), randomFloat(bounds.y, bounds.w));
    }

    private static float randomFloat(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }

    public Vector4 getBounds(Vector2 entSize) {
        return new Vector4(
                this.pos.x - this.size.x / 2 + entSize.x / 2,
                this.pos.y - this.size.y / 2 + entSize.y / 2,
                this.pos.x + this.size.x / 2 - entSize.x / 2,
                this.pos.y + this.size.y / 2 - entSize.y / 2
        );
    }

    @Override
    public void readSerializedEntityMetadata(EntityMetadata metadata) {
        this.spawnType = metadata.getJson().get("spawnType").getAsInt();
        this.spawnCount = metadata.getJson().get("spawnCount").getAsInt();
        this.entityHalfSize = metadata.getJson().get("entHalfSize").getAsFloat();
    }
}
