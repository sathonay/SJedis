package com.sjedis.common.connection.implementations;

import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.handler.PacketHandlers;

import java.net.Socket;

public class PacketConnection extends BasicConnection {

    private final PacketHandlers<PacketConnection> packetHandler;

    public PacketConnection(Socket socket, PacketHandlers<PacketConnection> packetHandler) {
        super(socket);
        this.packetHandler = packetHandler;
    }

    @Override
    protected void interpretObject(Object object) {
        if (object instanceof Packet) interpretPacket((Packet) object);
    }

    protected void interpretPacket(Packet packet) {
        packetHandler.handle(this, packet);
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
