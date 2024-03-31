package com.net.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * map工具类
 * @author wxy
 */
public class MapUtils {

    public static <K, V> Map<K, V> createHashMap(K k, V v) {
        Map<K, V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    public static <K, V> Map<K, V> createHashMap(List<Map<K, V>> maps) {
        Map<K, V> map = new HashMap<>();
        for (Map<K, V> kvMap : maps) {
            map.putAll(kvMap);
        }
        return map;
    }
}
