package com.sjedis.client.api.builders;

import com.sjedis.client.api.manager.ConnectionConsumersManager;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.PasswordPacket;
import com.sjedis.common.packet.RequestPacket;
import com.sjedis.common.packet.SetPacket;
import com.sjedis.common.response.Response;
import com.sjedis.client.api.Connection;
import com.sjedis.client.api.models.Multi;
import com.sjedis.client.api.models.PreparedSet;
import javafx.util.Pair;
import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Data
public class ConnectionBuilder implements Connection{

    private final Socket socket;
    private final List<Connection> connections;
    private final Map<UUID, CompletableFuture<Response>> futureMap = new HashMap<>();

    public ConnectionBuilder(Socket socket, String password, List<Connection> connections) {
        this.socket = socket;
        this.connections = connections;
        connections.add(this);
        initStreams();
        send(new PasswordPacket(password));
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread thread;

    private void initStreams() {
        try {
            socket.setTcpNoDelay(true);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            initConnectionThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConnectionThread() {
        thread = new Thread(this::waitObject);
        thread.start();
    }

    private void waitObject() {
        while (true) handleObject().ifPresent(this::interpretObject);
    }

    private Optional<Object> handleObject() {
        return Optional.ofNullable(readObject());
    }

    private Object readObject() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            close();
            return null;
        }
    }

    private void interpretObject(Object object) {
        handleConsumer(object).ifPresent(consumer -> consumer.accept(new Pair<>(this, object)));
    }

    private Optional<Consumer<Pair<Connection, Object>>> handleConsumer(Object object) {
        return Optional.ofNullable(ConnectionConsumersManager.getConsumer(object.getClass()));
    }

    @Override
    public void send(Packet... packets) {
        for (Packet packet : packets) sendSerializable(packet);
    }

    private void sendSerializable(Serializable serializable) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(serializable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String key, Object value) {
        set(Collections.singletonMap(key, value));
    }

    @Override
    public void set(Multi<String> keys, Object value) {
        set(new PreparedSet().set(keys, value));
    }

    @Override
    public void set(Map<String, Object> map) {
        send(new SetPacket(map));
    }


    @Override
    public void set(PreparedSet preparedSet) {
        set(preparedSet.toMap());
    }

    @Override
    public CompletableFuture<Response> get(String... keys) {
        return buildRequest(UUID.randomUUID(), keys);
    }

    public CompletableFuture<Response> get(Consumer<Response> consumer, String... keys) {
        return buildRequest(consumer, UUID.randomUUID(), keys);
    }

    private CompletableFuture<Response> buildRequest(Consumer<Response> consumer, UUID requestID, String... keys) {
        CompletableFuture<Response> future = futureMap.compute(requestID, (uuid, completableFuture) -> future(consumer));
        send(new RequestPacket(requestID, keys));
        return future;
    }

    private <T> CompletableFuture<T> future(Consumer<T> consumer) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    consumer.accept(future.get());

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                stop();
            }
        }.start();
        return future;
    }

    private CompletableFuture<Response> buildRequest(UUID requestID, String... keys) {
        CompletableFuture<Response> future = futureMap.compute(requestID, (uuid, completableFuture) -> new CompletableFuture<>());
        send(new RequestPacket(requestID, keys));
        return future;
    }

    @Override
    public void close() {
        System.out.println("connection closed");
        connections.remove(this);
        futureMap.clear();

        if (thread != null) thread.stop();

        try {
            outputStream.close();
        } catch (IOException e) {
        }

        try {
            inputStream.close();
        } catch (IOException e) {
        }

        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
