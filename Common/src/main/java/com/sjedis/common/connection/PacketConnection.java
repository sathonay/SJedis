package com.sjedis.common.connection;

import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.packet.Packet;

import java.net.Socket;
import java.util.Optional;
import java.util.function.BiConsumer;

public class PacketConnection extends BasicConnection {

    private final PacketHandlerMap<PacketConnection> handlerMap;

    public PacketConnection(Socket socket, PacketHandlerMap<PacketConnection> handlerMap) {
        super(socket);
        this.handlerMap = handlerMap;
    }

    @Override
    protected void interpretObject(Object object) {
        if (object instanceof Packet) interpretPacket((Packet) object);
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet> Optional<BiConsumer<PacketConnection, T>> handleConsumer(Packet packet) {
        return Optional.ofNullable((BiConsumer<PacketConnection, T>) handlerMap.getConsumer(packet.getClass()));
    }

    protected void interpretPacket(Packet packet) {
        handleConsumer(packet).ifPresent(consumer ->
            consumer.accept(this, packet)
        );
    }
}
