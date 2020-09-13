package com.sjedis.server.model;


import com.sjedis.server.Server;
import com.sjedis.server.threads.SocketThread;

import java.io.*;
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
    private OutputStream outputStream;
    private InputStream inputStream;

    public CacheSocket(Socket socket, Server server) {
        this.socket = socket;
        this.id = String.valueOf(SHARED_RANDOM.nextInt());
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            (this.socketThread = new SocketThread(this, server)).start();
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

        System.out.println("Connection close " + id);
        CACHE_SOCKET_MAP.remove(this.id);
        socketThread.stop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSerializable(Serializable serializable) throws IOException {
        ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
        objectOutput.writeObject(serializable);
        //System.out.println(serializable.getClass().getName());
    }

    public Object handleObject() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return objectInputStream.readObject();
    }

    public static Collection<CacheSocket> getCacheSockets() {
        return CACHE_SOCKET_MAP.values();
    }

    public static CacheSocket getCacheSocket(String id) {
        return CACHE_SOCKET_MAP.get(id);
    }
}
