package com.sjedis.server.connection;

import com.sjedis.common.connection.implementations.PacketConnection;
import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.PasswordPacket;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

public class ClientConnection extends PacketConnection {


    public ClientConnection(Socket socket, PacketHandlerMap handlerMap) {
        super(socket, handlerMap);
    }

    @Getter @Setter
    private boolean auth;

    @Override
    protected void interpretPacket(Packet packet) {
        if (auth || packet instanceof PasswordPacket) super.interpretPacket(packet);
    }

    @Override
    public void close() {

        System.out.println("close connection " + socket.getInetAddress().getHostName() + "@" + socket.getPort());

        super.close();
    }
}
