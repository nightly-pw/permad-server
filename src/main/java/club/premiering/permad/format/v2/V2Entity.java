package club.premiering.permad.format.v2;

import club.premiering.permad.math.Vector2;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class V2Entity {
    public int entType;
    public Vector2 entPos = new Vector2();
    public Vector2 entSize = new Vector2();
    public float entRot;
    public JsonObject metadata;
}
