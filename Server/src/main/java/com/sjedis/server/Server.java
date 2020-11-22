package com.sjedis.server;

import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.packet.PasswordPacket;
import com.sjedis.common.packet.RequestPacket;
import com.sjedis.common.packet.ResponsePacket;
import com.sjedis.common.packet.SetPacket;
import com.sjedis.common.response.Response;
import com.sjedis.server.connection.ClientConnection;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Server {

    @Getter
    private static Server INSTANCE;

    private final int port;
    private String password;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    private final PacketHandlerMap<ClientConnection> handlerMap = new PacketHandlerMap<>();

    public Server(int port, String password) {
        this.port = port;
        this.password = password;

        initServer();
    }

    private ServerSocket serverSocket;

    private void initServer() {
        initHandlerMap();
        this.serverSocket = buildServerSocket();
        initConnectionThread();
        Runtime.getRuntime().addShutdownHook(buildShutdownThread());

        INSTANCE = this;
    }

    private void initHandlerMap() {
        handlerMap.put(PasswordPacket.class, (connection, packet) -> {

            System.out.println("password check");
            if (connection.isAuth()) return;
            Server server = Server.getINSTANCE();
            if ((server.getPassword() == null || server.getPassword().equals(packet.password))) connection.setAuth(true);
            else connection.close();
        });

        handlerMap.put(SetPacket.class, (connection, packet) -> Server.getINSTANCE().getCache().putAll(packet.map));

        handlerMap.put(RequestPacket.class, (connection, packet) -> {
            Map<String, Object> responseMap = new HashMap<>();
            Map<String, Object> cache = Server.getINSTANCE().getCache();
            for (String key : packet.keys) responseMap.put(key, cache.get(key));
            System.out.println("send response");
            connection.send(new ResponsePacket(packet.requestID, new Response(responseMap)));
        });
    }

    private ServerSocket buildServerSocket() {
        try {
            System.out.println("Starting SJedis server on " + port);
            return new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private void initConnectionThread() {
        new Thread(){

            @Override
            public void run() {
                waitConnection();
            }

            private void waitConnection() {
                while (true) handleConnection().ifPresent(this::buildClientConnection);
            }

            private void buildClientConnection(Socket socket) {
                System.out.println("new connection from " + socket.getInetAddress().getHostName() + "@" + socket.getPort());
                new ClientConnection(socket, handlerMap);
            }

            private Optional<Socket> handleConnection() {
                return Optional.ofNullable(handleSocket());
            }

            private Socket handleSocket() {
                try {
                    System.out.println("waiting connection...");
                    return serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.start();
    }

    private Thread buildShutdownThread() {
        return new Thread(() -> System.out.println("bye bye !"));
    }
}
