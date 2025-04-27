package com.sjedis.common.connection.implementations;


import com.sjedis.common.connection.Connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public abstract class BasicConnection implements Connection {


    protected final Socket socket;
    private final List<CompletableFuture<Object>> futures = new ArrayList<>();

    protected ObjectOutputStream outputStream;
    protected ObjectInputStream inputStream;
    private Thread thread;

    public BasicConnection(Socket socket) {
        try {
            this.socket = enableTCP(socket);
            initStreams();
            initConnectionThread();
        } catch (Exception e) {
            e.printStackTrace();
            throw null;
        }
    }

    @Override
    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    private Socket enableTCP(Socket socket) throws Exception {
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }

    private void initStreams() throws Exception {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initConnectionThread() {
        thread = new Thread(this::handleObjects);
        thread.start();
    }

    private void handleObjects() {
        try {
            do {
                waitObject().ifPresent(object -> {
                    ForkJoinPool.commonPool().execute(() -> {
                        futures.forEach(future -> future.complete(object));
                        futures.clear();
                    });
                    interpretObject(object);
                });
            }
            while (true);
        } catch (Exception e) {
            close();
        }
    }

    private Optional<Object> waitObject() throws Exception {
        return Optional.ofNullable(readObject());
    }

    protected abstract void interpretObject(Object object);


    @Override
    public void send(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Serializable) writeObject((Serializable) object);
            else throw new IllegalArgumentException("Object " + object.getClass().getName() + " is not serializable");
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

        if (thread != null) thread.interrupt();

        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
