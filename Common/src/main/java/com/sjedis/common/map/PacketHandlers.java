package com.sjedis.common.map;

import com.sjedis.common.connection.Connection;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.PacketRegistry;

import java.util.function.BiConsumer;

public class PacketHandlers<C extends Connection> extends PacketHandlerMap {

    private final PacketHandler<C, ? extends Packet>[] handlers = new PacketHandler[PacketRegistry.values().length];

    public <P extends Packet> void setHandler(Class<P> clazz, PacketHandler<C, P> packetHandler) {
        for (PacketRegistry packet : PacketRegistry.values()) {
            if (clazz == packet.getPacketClass())handlers[packet.ordinal()] = packetHandler;
        }
    }

    public <P extends Packet> void handle(C connection, P packet) {
        PacketHandler<C, P> handler = (PacketHandler<C, P>) handlers[packet.id];
        if (handler != null) handler.handle(connection, packet);
    }
}
