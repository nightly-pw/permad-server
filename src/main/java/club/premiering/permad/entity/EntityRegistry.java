package club.premiering.permad.entity;

import club.premiering.permad.entity.spawners.EntityLavaSpike;
import club.premiering.permad.util.InstanceRegistry;

public class EntityRegistry {
    public static final InstanceRegistry<Integer, Entity> ENTITY_REGISTRY = new InstanceRegistry<>();

    static {
        ENTITY_REGISTRY.register(1, EntityPlayer.class);
        ENTITY_REGISTRY.register(2, EntityObstacle.class);
        ENTITY_REGISTRY.register(3, EntityLava.class);
        ENTITY_REGISTRY.register(4, EntityTeleporter.class);
        ENTITY_REGISTRY.register(5, EntityBouncer.class);
        ENTITY_REGISTRY.register(6, EntityText.class);
        ENTITY_REGISTRY.register(7, EntitySpawnerArea.class);
        ENTITY_REGISTRY.register(8, EntityLavaSpike.class);
        ENTITY_REGISTRY.register(10, EntityMoving.class);
        ENTITY_REGISTRY.register(11, EntityRotating.class);
    }
}
