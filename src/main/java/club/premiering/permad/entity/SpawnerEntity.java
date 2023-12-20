package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;
import lombok.Getter;
import lombok.Setter;

// Defines an area in which a specific type of entities are randomly spawned in the area
public abstract class SpawnerEntity extends RigidEntity {
    @Getter
    @Setter
    private Vector2 bounceVelocity = new Vector2(2f, 2f);
    @Getter
    @Setter
    private EntitySpawnerArea spawnerArea;

    @Override
    public void doTick() {
        var bounds = this.spawnerArea.getBounds(this.size);
        if (this.pos.x < bounds.x) {
            this.bounceOffCollision(new AABBCollisionInfo(false, false, true, false), this.getBounceVelocity());
        }
        if (this.pos.x > bounds.z) {
            this.bounceOffCollision(new AABBCollisionInfo(true, false, false, false), this.getBounceVelocity());
        }
        if (this.pos.y < bounds.y) {
            this.bounceOffCollision(new AABBCollisionInfo(false, false, false, true), this.getBounceVelocity());
        }
        if (this.pos.y > bounds.w) {
            this.bounceOffCollision(new AABBCollisionInfo(false, true, false, false), this.getBounceVelocity());
        }

        super.doTick();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        this.spawnerArea.onChildRemoved(this);
    }
}
