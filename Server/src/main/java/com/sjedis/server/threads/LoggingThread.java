package com.sjedis.server.threads;


import com.sjedis.server.Server;
import com.sjedis.server.model.CacheSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggingThread extends Thread{

    private final ServerSocket serverSocket;

    public LoggingThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                CacheSocket cacheSocket = new CacheSocket(socket);
                System.out.println("logging [ip: " + /*socket.getInetAddress() +*/ ", id: " + cacheSocket.getId() + "]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
