package com.sjedis.server;

import com.sjedis.server.threads.LoggingThread;
import com.sjedis.server.threads.ShutdownThread;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private ServerSocket serverSocket;

    public Server() {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        try {

            this.serverSocket = new ServerSocket(15342);

            Thread loggingThread = new LoggingThread(this.serverSocket);

            loggingThread.start();

            System.out.println("SJedis server started!");
            return;
        } catch (IOException e) {
            System.out.println("init failed!");
            System.exit(0);
        }
    }
}
