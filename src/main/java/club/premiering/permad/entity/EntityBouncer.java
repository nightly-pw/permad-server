package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;

public class EntityBouncer extends RigidEntity {
    private static final Vector2 COLLISION_BOUNCE_VEL = new Vector2(35f, 35f);

    public EntityBouncer() {
        this.size = new Vector2(600, 100);
        this.setSolid(true);
    }

    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {
        if (!(entity instanceof EntityPlayer player))
            return;
        player.bounceOffCollision(colInfo.copy().invert(), COLLISION_BOUNCE_VEL);
    }
}
