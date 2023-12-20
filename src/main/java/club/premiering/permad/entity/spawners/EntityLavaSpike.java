package club.premiering.permad.entity.spawners;

import club.premiering.permad.entity.*;

public class EntityLavaSpike extends SpawnerEntity {
    public EntityLavaSpike() {
        this.setSolid(false);
        this.setCollideWithSolids(false);
        this.setCanBeOutsideWorld(false);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.velocity = this.getBounceVelocity().clone();
        this.velocity.x = Math.random() > 0.5 ? -this.velocity.x : this.velocity.x;
        this.velocity.y = Math.random() > 0.5 ? -this.velocity.y : this.velocity.y;
    }

    @Override
    public void doTick() {
        this.pos.x += this.velocity.x;
        this.pos.y += this.velocity.y;

        super.doTick();
    }

    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {
        if (entity instanceof EntityPlayer player) {
            player.killPlayer(colInfo);
        }
        // TODO: 9/10/2023 Bouncing off of obstacles is broken because of the weird physics drop-in
        /*else if (entity.isSolid() && entity instanceof EntityObstacle) {
            this.bounceOffCollision(colInfo, this.getBounceVelocity());
        }*/
    }
}
