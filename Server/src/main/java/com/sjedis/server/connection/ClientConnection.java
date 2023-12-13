package com.sjedis.server.connection;

import com.sjedis.common.connection.implementations.PacketConnection;
import com.sjedis.common.packet.handler.PacketHandlers;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.PasswordPacket;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

public class ClientConnection extends PacketConnection {


    public ClientConnection(Socket socket, PacketHandlers packetHandlers) {
        super(socket, packetHandlers);
    }

    @Getter @Setter
    private boolean auth;

    @Override
    protected void interpretObject(Object object) {
        if (!auth && !(object instanceof PasswordPacket)) close(getNameAndPort() + " attempted to bypass the auth system");
        else super.interpretObject(object);
    }

    @Override
    protected void interpretPacket(Packet packet) {
        if (auth || packet instanceof PasswordPacket) super.interpretPacket(packet);
    }

    public String getNameAndPort() {
        return socket.getInetAddress().getHostName() + "@" + socket.getPort();
    }

    public void close(String message) {
        System.out.println(message);
        close();
    }

    @Override
    public void close() {
        System.out.println("close connection " + getNameAndPort());
        super.close();
    }
}
