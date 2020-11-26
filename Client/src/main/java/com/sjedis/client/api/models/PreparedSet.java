package com.sjedis.client.api.models;

import java.util.HashMap;
import java.util.Map;

public class PreparedSet {

    private final Map<String, Object> setMap;

    public PreparedSet() {
        setMap = new HashMap<>();
    }

    public PreparedSet(Map<String, Object> map) {
        setMap = map;
    }

    public PreparedSet set(String key, Object value) {
        setMap.put(key, value);
        return this;
    }

    public PreparedSet set(Multi<String> keys, Object value) {
        keys.toList().forEach(key -> set(key, value));
        return this;
    }
    
    
    public PreparedSet set(Map<String, Object> map) {
        setMap.putAll(map);
        return this;
    }

    public Map<String, Object> toMap() {
        return setMap;
    }
}
