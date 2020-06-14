package net.vpc.hadralang.stdlib.ext;

import java.util.Map;

public class MapExtensions {
    public static <T, V> V getAt(Map<T, V> map, T t) {
        return map.get(t);
    }

    public static <T, V> V setAt(Map<T, V> map, T t, V v) {
        return map.put(t, v);
    }

}
