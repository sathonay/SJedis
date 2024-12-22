package com.sjedis.common.connection.implementations;


import com.sjedis.common.connection.Connection;
import com.sjedis.common.util.AESUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public abstract class BasicConnection implements Connection {

    private final String password;

    protected final Socket socket;
    private final List<CompletableFuture<Object>> futures = new ArrayList<>();

    public BasicConnection(Socket socket, String password) {
        this.socket = enableTCP(socket);
        this.password = password;
        initStreams();
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

    private void initStreams() {
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
            try {
                return AESUtil.decryptObject("AES/CBC/PKCS5Padding" , (SealedObject) objectInputStream.readObject(), AESUtil.getKeyFromPassword(password));
            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException
                    | InvalidKeySpecException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
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
            try {
                objectOutputStream.writeObject(AESUtil.encryptObject("AES/CBC/PKCS5Padding", object, AESUtil.getKeyFromPassword(password)));
            } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
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
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
