package club.premiering.permad.format.v2;

import club.premiering.permad.entity.*;
import club.premiering.permad.entity.spawners.EntityLavaSpike;
import club.premiering.permad.math.Vector2;
import club.premiering.permad.math.Vector4;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class V2MapDataSkapConverter extends V2MapData {
    //If set to false, then all obstacle colors won't work (does not affect blocks!)
    private static final boolean OBSTACLES_USE_CUSTOM_COLOR = false;

    @Override
    public void loadMapData(byte[] mapData) {
        super.loadMapData(this.convertMapFromSkap(new String(mapData)).getBytes(StandardCharsets.UTF_8));
    }

    public String convertMapFromSkap(String skapMap) {
        JsonObject json = GSON.fromJson(skapMap, JsonObject.class);

        var v2Map = new V2Map();
        v2Map.worlds = new ArrayList<>();

        var settings = json.getAsJsonObject("settings");
        v2Map.metadata.name = settings.get("name").getAsString();
        v2Map.metadata.creator = settings.get("creator").getAsString();

        var worlds = json.getAsJsonArray("maps");
        var nameOfSpawn = settings.get("spawnArea").getAsString();
        var spawnPos = readSkapVec2(settings.getAsJsonArray("spawnPosition"));

        int worldIdCounter = 0;

        for (int i = 0; i < worlds.size(); i++) {
            var world = new V2World();
            world.entities = new ArrayList<>();
            world.worldId = worldIdCounter;
            worldIdCounter++;
            var sWorld = worlds.get(i).getAsJsonObject();
            world.worldName = sWorld.get("name").getAsString();

            if (world.worldName.equals(nameOfSpawn)) {
                world.worldSpawn = spawnPos;
                v2Map.spawnWorldId = world.worldId;
            }

            var worldSize = readSkapVec2(sWorld.get("size").getAsJsonArray());
            world.worldSize = new Vector4(0, 0, worldSize.x, worldSize.y);
            var entities = sWorld.getAsJsonArray("objects");
            for (int j = 0; j < entities.size(); j++) {
                var sEnt = entities.get(j).getAsJsonObject();
                var ent = createEntity(sEnt, worlds);
                if (ent != null)
                    world.entities.add(ent);
            }
            v2Map.worlds.add(world);
        }
        return GSON.toJson(v2Map);
    }

    private V2Entity createEntity(JsonObject sEnt, JsonArray worlds) {
        var sType = sEnt.get("type").getAsString();
        var type = 0;
        var ent = new V2Entity();
        ent.metadata = new JsonObject();
        var size = readSkapVec2(sEnt.getAsJsonArray("size"));
        var pos = readSkapVec2(sEnt.getAsJsonArray("position")).add(size.clone().divide(2, 2));
        ent.entSize = size;
        ent.entPos = pos;
        switch (sType) {
            case "obstacle":
                type = 2;
                break;
            case "block":
                type = 2;
                ent.metadata.addProperty("noClip", !sEnt.get("collide").getAsBoolean());
                //System.out.println(world.worldName);
                ent.metadata.addProperty("color", readSkapColor(sEnt.getAsJsonArray("color"), sEnt.get("opacity")).getRGB());
                break;
            case "lava":
                type = 3;
                break;
            case "slime":
                type = 5;
                break;
            case "spawner":
                var spawnType = this.convertSpawnerType(sEnt.get("entityType").getAsString());
                if (spawnType == -1)//We don't support this entity
                    return null;
                type = 7;

                ent.metadata.addProperty("spawnType", spawnType);
                ent.metadata.addProperty("spawnCount", sEnt.get("number").getAsInt());
                ent.metadata.addProperty("entHalfSize", convertSkapUnits(sEnt.get("radius").getAsFloat()));
                break;
            case "text":
                type = 6;
                ent.metadata.addProperty("text", sEnt.get("text").getAsString());
                ent.metadata.addProperty("alignment", TextAlignment.CENTER.toByte());
                ent.metadata.addProperty("color", Color.WHITE.getRGB());
                break;
            case "teleporter":
                type = 4;
                ent.metadata.addProperty("id", sEnt.get("id").getAsInt());
                ent.metadata.addProperty("targetWorld", this.getIdFromWorldName(worlds, sEnt.get("targetArea").getAsString()));
                ent.metadata.addProperty("targetTp", sEnt.get("targetId").getAsInt());
                ent.metadata.addProperty("dir", sEnt.get("dir").getAsNumber());
                break;
            case "movingLava":
                type = 10;
                ent.metadata.addProperty("entType", EntityRegistry.ENTITY_REGISTRY.get(EntityLava.class));
                ent.metadata.add("points", this.convertMovingPoints(sEnt.getAsJsonArray("points")));
                break;
            case "rotatingLava":
                type = EntityRegistry.ENTITY_REGISTRY.get(EntityRotating.class);
                ent.metadata.addProperty("entType", EntityRegistry.ENTITY_REGISTRY.get(EntityLava.class));
                ent.metadata.add("rotPnt", writeVec2Meta(readSkapVec2(sEnt.getAsJsonArray("point"))));
                ent.metadata.addProperty("degPs", sEnt.get("speed").getAsFloat());
                ent.metadata.addProperty("startDeg", sEnt.get("startAngle").getAsFloat());
                break;
            default:
                return null;
        }
        ent.entType = type;
        return ent;
    }

    private static float convertSkapMovingSpeed(float speed) {
        // Skap uses u/s in moving objects (not delta time bound), while we want the velocity per tick
        // So we'll just convert the speed to our 60tps measure
        return convertSkapUnits(speed / 60);
    }

    private static JsonObject writeVec2Meta(Vector2 vec) {
        var obj = new JsonObject();
        obj.addProperty("x", vec.x);
        obj.addProperty("y", vec.y);
        return obj;
    }

    private JsonArray convertMovingPoints(JsonArray arr) {
        var n = new JsonArray();
        for (int i = 0; i < arr.size(); i++) {
            var ptObj = arr.get(i).getAsJsonObject();
            var pos = readSkapVec2(ptObj.getAsJsonArray("position"));
            var newPos = new JsonObject();
            newPos.addProperty("x", pos.x);
            newPos.addProperty("y", pos.y);
            newPos.addProperty("vel", convertSkapMovingSpeed(ptObj.get("vel").getAsFloat()));
            n.add(newPos);
        }
        return n;
    }

    private int convertSpawnerType(String type) {
        switch (type) {
            case "spike", "rotating", "harmless":
                return EntityRegistry.ENTITY_REGISTRY.get(EntityLavaSpike.class);
        }
        return -1;
    }

    private int getIdFromWorldName(JsonArray worldsArr, String worldName) {
        int worldIdCounter = 0;
        for (int i = 0; i < worldsArr.size(); i++) {
            var sWorld = worldsArr.get(i).getAsJsonObject();
            var name = sWorld.get("name").getAsString();
            if (name.equals(worldName))
                return worldIdCounter;
            worldIdCounter++;
        }
        return -1;
    }

    private V2World getWorldFromName(V2Map map, String name) {
        for (var world : map.worlds) {
            if (Objects.equals(world.worldName, name)) {
                return world;
            }
        }

        return null;
    }

    private static Vector2 readSkapVec2(JsonArray arr) {
        if (arr == null) {
            return new Vector2(convertSkapUnits(1), convertSkapUnits(1));
        }
        var x = arr.get(0).getAsFloat();
        var y = arr.get(1).getAsFloat();
        return new Vector2(convertSkapUnits(x), convertSkapUnits(y));
    }

    private static Color readSkapColor(JsonArray arr) {
        var r = arr.get(0).getAsInt();
        var g = arr.get(1).getAsInt();
        var b = arr.get(2).getAsInt();
        System.out.println(r + " " + g + " " + b);
        return new Color(r, g, b);
    }

    private static Color readSkapColor(JsonArray arr, JsonElement opacity) {
        var r = arr.get(0).getAsInt();
        var g = arr.get(1).getAsInt();
        var b = arr.get(2).getAsInt();
        var a = (int) (opacity.getAsDouble() * 255D);
        return new Color(r, g, b, a);
    }

    private static float convertSkapUnits(float units) {
        return (float) Math.floor(units * 5 * 48/30);
        //5 is an arbitrary number used to get a decent pixel size of entities,
        //and 48/30 converts the Skap player size to our player size (imperfectly, but to scale)
    }
}
