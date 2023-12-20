package club.premiering.permad.format;

import club.premiering.permad.math.Vector2;
import com.google.gson.JsonObject;
import lombok.Getter;

// Entity metadata will always be JSON
public final class EntityMetadata {
    @Getter
    protected final JsonObject json;

    public EntityMetadata(JsonObject json) {
        this.json = json;
    }

    public Vector2 getVector2(JsonObject vec) {
        var x = vec.get("x").getAsFloat();
        var y = vec.get("y").getAsFloat();
        return new Vector2(x, y);
    }
}
