package com.sjedis.common.connection;

import java.util.concurrent.CompletableFuture;

public interface Connection {

    void send(Object... objects);

    CompletableFuture<Object> catchObject();

    void close();

    boolean isClosed();
}
