package com.sjedis.server.model;


import com.sjedis.server.threads.SocketThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class CacheSocket {
    private static final Random SHARED_RANDOM = new Random();
    private static final Map<String, CacheSocket> CACHE_SOCKET_MAP = new ConcurrentHashMap<>();

    private final Socket socket;
    private final String id;
    private Thread socketThread;
    private PrintWriter printWriter;

    public CacheSocket(Socket socket) {
        this.socket = socket;
        this.id = String.valueOf(SHARED_RANDOM.nextInt());
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream());
            (this.socketThread = new SocketThread(this)).start();
            CACHE_SOCKET_MAP.put(this.id, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void destroy() {
        CACHE_SOCKET_MAP.remove(this.id);
        socketThread.stop();
    }

    public void send(String... lines) {
        for (String line : lines) printWriter.println(line);
        printWriter.flush();
    }

    public static Collection<CacheSocket> getCacheSockets() {
        return CACHE_SOCKET_MAP.values();
    }

    public static CacheSocket getCacheSocket(String id) {
        return CACHE_SOCKET_MAP.get(id);
    }
}
