package club.premiering.permad;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PermaConfig {
    public String mongoAddress;
    public boolean debugMode;
    public PermaGlobalMap[] globalMaps;
}
