package com.sjedis.server.client;

import com.sathonay.common.packet.Packet;
import com.sathonay.common.packet.PasswordPacket;
import javafx.util.Pair;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.function.Consumer;

@Data
public class ClientConnection {

    private final Socket socket;

    public ClientConnection(Socket socket) {
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

    private Thread initConnectionThread() {
        return new Thread(() ->
                handleObject().ifPresent(this::interpretObject)
        );
    }

    private Optional<Object> handleObject() {
        return Optional.ofNullable(readObject());
    }

    private Object readObject() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    private void interpretObject(Object object) {
        if (object instanceof Packet) interpretPacket((Packet) object);
    }

    private void interpretPacket(Packet packet) {
        if ((!login && !(packet instanceof PasswordPacket))) return; // TODO: close connection
        handleConsumer(packet).ifPresent(consumer -> {
            consumer.accept(new Pair<>(this, packet));
        });
    }

    private Optional<Consumer<Pair<ClientConnection, Packet>>> handleConsumer(Object object) {
        return Optional.ofNullable(ClientConsumers.getConsumer(object.getClass()));
    }
}
