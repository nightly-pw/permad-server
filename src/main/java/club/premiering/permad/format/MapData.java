package club.premiering.permad.format;

import club.premiering.permad.world.World;

public interface MapData {
    void loadMapData(byte[] mapData);
    boolean loadWorld(int id, World world);//Returns if loading was a success
    int getSpawnWorldId();
    MapMetadata getMetadata();
    int getWorldIdFromName(String worldName) throws RuntimeException;
}
