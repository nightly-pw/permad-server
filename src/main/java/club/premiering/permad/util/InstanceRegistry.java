package club.premiering.permad.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.SneakyThrows;

public class InstanceRegistry<K, V> {
    public final BiMap<K, Class<? extends V>> registry = HashBiMap.create();

    public void register(K k, Class<? extends V> v) {
        this.registry.put(k, v);
    }

    public Class<? extends V> get(K k) {
        var v = this.registry.get(k);
        if (v != null)
            return v;

        throw new RuntimeException("Could not find value for " + k);
    }

    public K get(Class<? extends V> v) {
        var k = this.registry.inverse().get(v);
        if (k != null)
            return k;
        throw new RuntimeException("Could not find value for " + v + " (inverse)");
    }

    @SneakyThrows
    public V create(K k) {
        var vClass = get(k);
        if (vClass == null)
            throw new RuntimeException("Could not find value for " + k);

        return vClass.getConstructor(new Class[] {}).newInstance();
    }
}
