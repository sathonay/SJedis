package com.sjedis.server.threads;

import com.sjedis.common.map.SJedisConcurrentHashMap;
import com.sjedis.common.map.SJedisMap;
import com.sjedis.common.packets.*;
import com.sjedis.server.Server;
import com.sjedis.server.model.CacheSocket;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SocketThread extends Thread {

    private static final SJedisMap<String, Object> CACHE_MAP = new SJedisConcurrentHashMap<>();

    private final CacheSocket cacheSocket;
    private final Server server;

    public SocketThread(CacheSocket cacheSocket, Server server) {
        this.cacheSocket = cacheSocket;
        this.server = server;
    }

    @Override
    public void run() {

        try {

            Object o = cacheSocket.handleObject();

            if (!(o instanceof PasswordPacket) || !server.getPassword().equals(((PasswordPacket) o).password)) {
                cacheSocket.sendSerializable(new AuthSuccessPacket(false));
                cacheSocket.destroy();
                return;
            } else {
                cacheSocket.sendSerializable(new AuthSuccessPacket(true));
            }

            cacheSocket.sendSerializable(new MapContentPacket(CACHE_MAP));

            //OutputStream outputStream = socket.getOutputStream();
            while (true) {
                Object object = cacheSocket.handleObject();
                /*if (object instanceof RequestValuePacket) {
                    //ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    //objectOutputStream.writeObject(CACHE_MAP.get(((RequestValuePacket) object).requestedValue));
                } else */if (object instanceof KeyValuePacket) {
                    KeyValuePacket packet = (KeyValuePacket) object;
                    Object value = packet.value;
                    CACHE_MAP.removeIfNullOrPut(packet.key, value);
                    System.out.println("["+cacheSocket.getId() + "] " + packet.key + " -> " + value);
                    Set<CacheSocket> socketSet = new HashSet<>(CacheSocket.getCacheSockets());
                    socketSet.remove(cacheSocket);
                    socketSet.forEach(cacheSocket1 -> {
                        try {
                            cacheSocket1.sendSerializable(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                if (object instanceof MultipleKeyValuePacket) {
                    MultipleKeyValuePacket packet = (MultipleKeyValuePacket) object;
                    Map<String, Object> map = packet.value;

                    //System.out.println("["+cacheSocket.getId() + "] " + "MultipleSet message coming i'm too busy for this shit");

                    map.forEach((key, value) -> {
                        CACHE_MAP.removeIfNullOrPut(key, value);
                        System.out.println("[" + cacheSocket.getId() + "] " + key + " -> " + value);
                    });

//                    Set<CacheSocket> socketSet = new HashSet<>(CacheSocket.getCacheSockets());
//                    socketSet.remove(cacheSocket);
//                    socketSet.forEach(cacheSocket1 -> {
//                        try {
//                            cacheSocket1.sendSerializable(packet);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
                    redistribute(cacheSocket, packet);
                }

                if (object instanceof UpdateNumberValuePacket) {
                    UpdateNumberValuePacket packet = (UpdateNumberValuePacket) object;

                    Object value = CACHE_MAP.get(packet.key);
                    if (CACHE_MAP.containsKey(packet.key) && value instanceof Number) {
                        CACHE_MAP.put(packet.key, (value = (double) CACHE_MAP.get(packet.key) + packet.value));
                    } else {
                        CACHE_MAP.put(packet.key, (value = packet.value));
                    }

                    System.out.println("["+cacheSocket.getId() + "] " + packet.key + " -> " + value);

                    KeyValuePacket newPacket = new KeyValuePacket(packet.key, value);

//                    Set<CacheSocket> socketSet = new HashSet<>(CacheSocket.getCacheSockets());
//                    socketSet.forEach(cacheSocket1 -> {
//                        try {
//                            cacheSocket1.sendSerializable(newPacket);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
                    redistribute(null, newPacket);
                }

            }
        } catch (IOException | ClassNotFoundException e) {
			cacheSocket.destroy();
        }
    }

    private void redistribute(CacheSocket toRemove, Serializable toDistribute) {
        Set<CacheSocket> socketSet = new HashSet<>(CacheSocket.getCacheSockets());
        if (toRemove != null) socketSet.remove(toRemove);
        socketSet.forEach(cacheSocket1 -> {
            try {
                cacheSocket1.sendSerializable(toDistribute);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
