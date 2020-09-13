package com.sjedis.common.map;

import java.util.concurrent.ConcurrentHashMap;

public class SJedisConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> implements SJedisMap<K, V> {
    @Override
    public V removeIfNullOrPut(K key, V value) {
        if (value == null) {
            return this.remove(key);
        } else {
            return this.put(key, value);
        }
    }
}
