package com.sjedis.server;

import com.sjedis.server.threads.LoggingThread;
import com.sjedis.server.threads.ShutdownThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private final String password;
    private final Integer port;

    public Server(String password, Integer port) {
        this.password = password;
        this.port = port;
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        try {

            this.serverSocket = new ServerSocket(port);

            Thread loggingThread = new LoggingThread(this);

            loggingThread.start();

            System.out.println("SJedis server started!");
        } catch (IOException e) {
            System.out.println("init failed!");
            System.exit(0);
        }
    }

    public String getPassword() {
        return password;
    }

    public Socket accept() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
