package com.sjedis.common.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Response implements Serializable {

    private final Map<String, Object> response;

    public Response(Map<String, Object> response) {
        this.response = response;
    }

    public <T> T get(String key) {
        return (T) response.get(key);
    }

    public <K, V> Map<K, V> toMap() {
        return new HashMap<>((Map<? extends K, ? extends V>) response);
    }
}
