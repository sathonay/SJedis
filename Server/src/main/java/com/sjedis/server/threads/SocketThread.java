package com.sjedis.server.threads;

import com.sjedis.common.KeyValuePacket;
import com.sjedis.common.RequestValuePacket;
import com.sjedis.server.model.CacheSocket;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketThread extends Thread {

    private static final Map<String, Object> CACHE_MAP = new ConcurrentHashMap<>();

    private final CacheSocket cacheSocket;

    public SocketThread(CacheSocket cacheSocket) {
        this.cacheSocket = cacheSocket;
    }

    @Override
    public void run() {

        final Socket socket = cacheSocket.getSocket();

        try {

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object object = objectInputStream.readObject();
                if (object instanceof RequestValuePacket) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(CACHE_MAP.get(((RequestValuePacket) object).requestedValue));
                } else if (object instanceof KeyValuePacket) {
                    KeyValuePacket packet = (KeyValuePacket) object;
                    Object value = packet.value;
                    if (value == null) {
                        CACHE_MAP.remove(packet.key);
                    } else {
                        CACHE_MAP.put(packet.key, packet.value);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
			cacheSocket.destroy();
        }
    }
}
