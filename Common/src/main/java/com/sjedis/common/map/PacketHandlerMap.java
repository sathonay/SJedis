package com.sjedis.common.map;

import com.sjedis.common.connection.Connection;
import com.sjedis.common.packet.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PacketHandlerMap<C extends Connection> {

    private final Map<Class<?>, BiConsumer<C, ? extends Packet>> map = new HashMap<>();

    public <T extends Packet> void put(Class<T> tClass, BiConsumer<C, T> consumer) {
        map.put(tClass, consumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> BiConsumer<C, T> getConsumer(Class<T> tClass) {
        return (BiConsumer<C, T>) map.get(tClass);
    }

    public void clear() {
        map.clear();
    }
}
