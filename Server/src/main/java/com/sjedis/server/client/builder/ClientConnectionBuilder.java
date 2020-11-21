package com.sjedis.server.client.builder;

import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.PasswordPacket;
import com.sjedis.server.client.manager.ClientConnectionConsumersManager;
import javafx.util.Pair;
import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.function.Consumer;

@Data
public class ClientConnectionBuilder {

    private final Socket socket;

    public ClientConnectionBuilder(Socket socket) {
        this.socket = socket;

        initClientConnection();
    }

    private boolean login = false;
    private OutputStream outputStream;
    private InputStream inputStream;

    private void initClientConnection() {
        initStreams();
    }

    private void initStreams() {
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            initConnectionThread();
            // TODO: add ClientConnection in memory
        } catch (IOException e) {
            // TODO: close socket connection
        }
    }

    private Thread thread;

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

    private void interpretObject(Object object) {
        if ((!login && !(object instanceof PasswordPacket))) return;
        handleConsumer(object).ifPresent(consumer -> consumer.accept(new Pair<>(this, object)));
    }

    private Optional<Consumer<Pair<ClientConnectionBuilder, Object>>> handleConsumer(Object object) {
        return Optional.ofNullable(ClientConnectionConsumersManager.getConsumer(object.getClass()));
    }

    public void close() {
        System.out.println("Connection close " + socket.getInetAddress().getHostName() + "@" + socket.getPort());

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
