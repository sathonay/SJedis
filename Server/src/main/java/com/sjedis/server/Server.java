package com.sjedis.server;

import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

@Data
public class Server {

    @Getter
    private static Server INSTANCE;

    private final int port;
    private String password;

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
                while (true) {
                    // TODO: create ClientConnection object & if password = null auto login
                    handleConnection().ifPresent(socket -> System.out.println("new connection " + socket.getInetAddress().getHostName()));
                }
            }

            private Optional<Socket> handleConnection() {
                return Optional.ofNullable(handleSocket());
            }

            private Socket handleSocket() {
                try {
                    return serverSocket.accept();
                } catch (IOException e) {
                    return null;
                }
            }
        }.start();
    }

    private Thread buildShutdownThread() {
        return new Thread(() -> System.out.println("bye bye !"));
    }
}
