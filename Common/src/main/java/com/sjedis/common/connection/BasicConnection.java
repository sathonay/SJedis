package com.sjedis.common.connection;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public abstract class BasicConnection implements Connection {

    private final Socket socket;
    private final List<CompletableFuture<Object>> futures = new ArrayList<>();

    public BasicConnection(Socket socket) {
        this.socket = enableTCP(socket);
        initConnection();
    }

    private Socket enableTCP(Socket socket) {
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return socket;
    }

    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;

    private void initConnection() {
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
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
        while (true) handleObject().ifPresent(object -> {
            ForkJoinPool.commonPool().execute(() -> {
                futures.forEach(future -> future.complete(object));
                futures.clear();
            });
            interpretObject(object);
        });
    }

    private Optional<Object> handleObject() {
        return Optional.ofNullable(readObject());
    }

    protected abstract void interpretObject(Object object);

    private Object readObject() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            close();
            return null;
        }
    }

    @Override
    public void send(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Serializable) sendSerializable((Serializable) object);
            else sendObject(object);
        }
    }

    protected void sendObject(Object object) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendSerializable(Serializable object) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Object> catchObject() {
        CompletableFuture<Object> future = new CompletableFuture<>();
        futures.add(future);
        return future;
    }

    @Override
    public void close() {
        futures.forEach(future -> future.cancel(false));
        futures.clear();

        if (thread != null) thread.stop();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
