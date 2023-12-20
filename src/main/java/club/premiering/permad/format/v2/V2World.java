package club.premiering.permad.format.v2;

import club.premiering.permad.math.Vector2;
import club.premiering.permad.math.Vector4;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class V2World {
    public String worldName;
    public int worldId;
    public Vector4 worldSize = new Vector4();
    public Vector2 worldSpawn = new Vector2();
    public List<V2Entity> entities;
}
