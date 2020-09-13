package com.sjedis.client;

import com.sjedis.client.event.PacketHandleEvent;

import java.util.Map;

public interface SJedis {

    boolean connect();

    void set(String key, Object value);

    void updateNumber(String key, double number);

    Object get(String key);

    Object getOrDefault(String key, Object defaultValue);

    Map<String, Object> getCache();

    void addHandler(PacketHandleEvent event);

    //Object getNet(String key);

    boolean close();

}
