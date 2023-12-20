package club.premiering.permad.format.v2;

import club.premiering.permad.entity.EntityRegistry;
import club.premiering.permad.world.World;
import club.premiering.permad.format.EntityMetadata;
import club.premiering.permad.format.MapData;
import club.premiering.permad.format.MapMetadata;
import com.google.gson.Gson;

public class V2MapData implements MapData {
    public static final Gson GSON = new Gson();

    private V2Map map;

    @Override
    public void loadMapData(byte[] mapData) {
        map = GSON.fromJson(new String(mapData), V2Map.class);
    }

    @Override
    public boolean loadWorld(int id, World world) {
        V2World v2World = null;

        for (var iter : map.worlds) {
            if (iter.worldId == id)
                v2World = iter;
        }

        if (v2World == null) {
            return false;
        }

        world.setWorldName(v2World.worldName);
        world.setWorldSpawn(v2World.worldSpawn);
        world.setWorldSize(v2World.worldSize);
        for (var v2Ent : v2World.entities) {
            var ent = EntityRegistry.ENTITY_REGISTRY.create(v2Ent.entType);
            ent.readSerializedEntityData(v2Ent.entPos, v2Ent.entSize, v2Ent.entRot);
            ent.readSerializedEntityMetadata(new EntityMetadata(v2Ent.metadata));
            world.addEntity(ent);
        }
        return true;
    }

    @Override
    public int getSpawnWorldId() {
        return map.spawnWorldId;
    }

    @Override
    public MapMetadata getMetadata() {
        return map.metadata;
    }

    @Override
    public int getWorldIdFromName(String worldName) throws RuntimeException {
        for (V2World world : map.worlds) {
            if (world.worldName.equalsIgnoreCase(worldName))
                return world.worldId;
        }
        throw new RuntimeException("World id not found from world name \"" + worldName + "\"");
    }
}
