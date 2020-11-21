package com.sjedis.server;

import com.sjedis.server.client.builder.ClientConnectionBuilder;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public Server(int port, String password) {
        this.port = port;
        this.password = password;

        initServer();
    }

    private ServerSocket serverSocket;

    private void initServer() {
        this.serverSocket = buildServerSocket();
        initConnectionThread();
        Runtime.getRuntime().addShutdownHook(buildShutdownThread());

        INSTANCE = this;
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
                new ClientConnectionBuilder(socket);
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
