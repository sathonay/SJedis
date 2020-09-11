package com.sjedis.client.builder;

import com.sjedis.client.SJedis;
import com.sjedis.common.KeyValuePacket;
import com.sjedis.common.RequestValuePacket;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class SJedisBuilder{

    private String ip;
    private int port;


    public SJedisBuilder setSJedisIp(String ip) {
        this.ip = ip;
        return this;
    }

    public SJedisBuilder setSJedisPort(int port) {
        this.port = port;
        return this;
    }

    public SJedis build() {
        return new SJedis() {

            private Optional<Socket> optional;
            private InputStream inputStream;
            private OutputStream outputStream;

            @Override
            public boolean connect() {
                try {
                    optional = Optional.of(new Socket(ip, port));
                    if (optional.isPresent()) {
                        Socket socket = optional.get();
                        socket.setTcpNoDelay(true);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void set(String key, Object value) {

                optional.ifPresent(socket -> {
                    try {
                        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        //objectOutputStream.writeObject(Collections.singletonMap(key, value));
                        //objectOutputStream.writeObject(new KeyValuePacket(key, value));
                        sendSerializable(new KeyValuePacket(key, value));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public Object get(String key) {

                if (optional.isPresent()) {
                    try {

                        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        //objectOutputStream.writeObject(new String[]{key});
                        //objectOutputStream.writeObject(new RequestValuePacket(key));

                        sendSerializable(new RequestValuePacket(key));

                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        return objectInputStream.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        //e.printStackTrace();
                        return null;
                    }
                }

                return null;
            }

            private void sendSerializable(Serializable serializable) throws IOException {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                //objectOutputStream.writeObject(new String[]{key});
                objectOutputStream.writeObject(serializable);
            }

            @Override
            public boolean close() {
                optional.ifPresent(socket -> {
                    if (socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        };
    }
}
