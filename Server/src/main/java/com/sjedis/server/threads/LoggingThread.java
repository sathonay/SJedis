package com.sjedis.server.threads;


import com.sjedis.server.Server;
import com.sjedis.server.model.CacheSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggingThread extends Thread{

    private final Server server;

    public LoggingThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = server.accept();
            if (socket == null) continue;
            CacheSocket cacheSocket = new CacheSocket(socket, server);
            System.out.println("New Connection " + cacheSocket.getId());
        }
    }
}
