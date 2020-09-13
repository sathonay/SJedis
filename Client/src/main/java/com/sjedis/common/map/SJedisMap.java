package com.sjedis.common.map;

import java.util.Map;

public interface SJedisMap<K, V> extends Map<K, V> {

    /**
     *
     * @param key
     * @param value
     * @return
     */
    V removeIfNullOrPut(K key, V value);

}
