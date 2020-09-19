package com.sjedis.client.builder;

import com.sjedis.client.PreparedSet;

import java.util.LinkedHashMap;
import java.util.Map;

public class PreparedSetBuilder{

    private final Map<String, Object> MAP = new LinkedHashMap<>();

    public PreparedSetBuilder set(String key, Object value) {
        MAP.put(key, value);
        return this;
    }

    public PreparedSet build() {
        return new PreparedSet() {
            @Override
            public Map<String, Object> toMap() {
                return PreparedSetBuilder.this.MAP;
            }
        };
    }
}
