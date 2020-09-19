package com.sjedis.client.builder;

import com.sjedis.client.PreparedSet;
import com.sjedis.client.SJedis;
import com.sjedis.client.event.PacketHandleEvent;
import com.sjedis.common.map.SJedisConcurrentHashMap;
import com.sjedis.common.map.SJedisMap;
import com.sjedis.common.packets.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class SJedisBuilder{

    private String ip;
    private int port;
    private String password;


    public SJedisBuilder setSJedisIp(String ip) {
        this.ip = ip;
        return this;
    }

    public SJedisBuilder setSJedisPort(int port) {
        this.port = port;
        return this;
    }

    public SJedisBuilder setSJedisPassword(String password) {
        this.password = password;
        return this;
    }

    private boolean isValid() {
        return ip != null && password != null;
    }

    public SJedis build() throws Exception {

        if (!isValid()) {
            throw  new Exception("Ip or Password are equals to null");
        }

        return new SJedis() {

            private final SJedisMap<String, Object> CACHE_MAP = new SJedisConcurrentHashMap<>();
            private Optional<Socket> optional;
            private InputStream inputStream;
            private OutputStream outputStream;
            private Thread thread;
            private final List<PacketHandleEvent> HANDLERS = new ArrayList<>();

            @Override
            public boolean connect() {
                try {


                    if (optional != null && optional.isPresent() && optional.get().isConnected()) return false;

                    optional = Optional.of(new Socket(ip, port));
                    if (optional.isPresent()) {
                        Socket socket = optional.get();
                        socket.setTcpNoDelay(true);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                    }

                    sendSerializable(new PasswordPacket(password));

                    boolean authSuccess = false;

                    try {
                        Object object = this.handleObject();

                        if (object instanceof AuthSuccessPacket) {
                            AuthSuccessPacket packet = (AuthSuccessPacket) object;
                            if (packet.valid) authSuccess = true;
                        }

                        if (authSuccess) {
                            object = this.handleObject();

                            if (object instanceof MapContentPacket) {
                                MapContentPacket packet = (MapContentPacket) object;
                                CACHE_MAP.putAll(packet.map);
                            }
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (authSuccess) {
                        thread = new Thread(() -> {
                            while (true) {
                                try {
                                    Object object = this.handleObject();
                                    System.out.println(object);
                                    if (object instanceof KeyValuePacket) {
                                        KeyValuePacket packet = (KeyValuePacket) object;
                                        CACHE_MAP.put(packet.key, packet.value);
                                        HANDLERS.forEach(event -> event.handleKeyValuePacketEvent(packet));
                                        continue;
                                    }

                                    if (object instanceof MultipleKeyValuePacket) {
                                        MultipleKeyValuePacket packet = (MultipleKeyValuePacket) object;
                                        Map<String, Object> map = packet.value;
                                        map.forEach(CACHE_MAP::removeIfNullOrPut);
                                    }

                                    //HANDLERS.forEach(event -> event.handleObjectEvent(object));
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }

                    return authSuccess;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public SJedis send(String key, Object value) {
                CACHE_MAP.removeIfNullOrPut(key, value);
                this.sendSerializable(new KeyValuePacket(key, value));
                return this;
            }

            @Override
            public SJedis send(PreparedSet preparedSet) {
                this.send(preparedSet.toMap());
                return this;
            }

            @Override
            public SJedis send(Map<String, Object> map) {
                this.CACHE_MAP.putAll(map);
                this.sendSerializable(new MultipleKeyValuePacket(map));
                return this;
            }

            @Override
            public SJedis updateNumber(String key, double number) {
                this.sendSerializable(new UpdateNumberValuePacket(key, number));
                return this;
            }

            @Override
            public Object get(String key) {
                return CACHE_MAP.get(key);
            }

            @Override
            public Object getOrDefault(String key, Object defaultValue) {
                return CACHE_MAP.getOrDefault(key, defaultValue);
            }

            private void sendSerializable(Serializable serializable) {
                if (!optional.isPresent()) return;
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(serializable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Map<String, Object> getCache() {
                return new HashMap<>(CACHE_MAP);
            }

            @Override
            public void addHandler(PacketHandleEvent event) {
                HANDLERS.add(event);
            }

            private Object handleObject() throws IOException, ClassNotFoundException {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                return objectInputStream.readObject();
            }

            @Override
            public boolean close() {

                if (thread != null) thread.stop();
                optional.ifPresent(socket -> {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return false;
            }
        };
    }
}
