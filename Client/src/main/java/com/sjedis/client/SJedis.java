package com.sjedis.client;

import com.sjedis.client.event.PacketHandleEvent;

import java.util.Map;

public interface SJedis {

    boolean connect();

    SJedis send(String key, Object value);

    SJedis send(PreparedSet set);

    SJedis send(Map<String, Object> map);

    SJedis updateNumber(String key, double number);

    Object get(String key);

    Object getOrDefault(String key, Object defaultValue);

    Map<String, Object> getCache();

    void addHandler(PacketHandleEvent event);

    boolean close();
}
