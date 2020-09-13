package com.sjedis.server;

import com.sjedis.server.threads.LoggingThread;
import com.sjedis.server.threads.ShutdownThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private final String password;

    public Server(String[] args) {
        password = args[0];
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        try {

            this.serverSocket = new ServerSocket(15342);

            Thread loggingThread = new LoggingThread(this);

            loggingThread.start();

            System.out.println("SJedis server started!");
            return;
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
