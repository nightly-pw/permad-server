package club.premiering.permad.entity;

import club.premiering.permad.math.Vector2;

public class EntityLava extends RigidEntity {
    public EntityLava() {
        this.size = new Vector2(600, 100);
        this.setSolid(true);
    }

    @Override
    public void onCollision(RigidEntity entity, AABBCollisionInfo colInfo) {
        if (entity instanceof EntityPlayer player) {
            player.killPlayer(colInfo);
        }
    }
}
