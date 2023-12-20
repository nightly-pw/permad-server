package club.premiering.permad.format.v2;

import club.premiering.permad.format.MapMetadata;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class V2Map {
    public int spawnWorldId;
    public List<V2World> worlds;
    public MapMetadata metadata = new MapMetadata();
}
