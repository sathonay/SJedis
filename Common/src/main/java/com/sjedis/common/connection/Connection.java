package com.sjedis.common.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CompletableFuture;

public interface Connection {

    void send(Object... objects);

    CompletableFuture<Object> catchObject();

    ObjectInputStream getInputStream();

    ObjectOutputStream getOutputStream();

    default Object readObject() throws Exception {
        try {
            return getInputStream().readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    default void writeObject(Object object) {
        try {
            getOutputStream().writeObject(object);
            getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close();

    boolean isClosed();
}
