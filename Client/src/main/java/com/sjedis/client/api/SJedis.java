package com.sjedis.client.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface SJedis {

    void setHost(String host);
    String getHost();

    void setPort(int port);
    int getPort();

    void setPassword(String password);
    String getPassword();

    Optional<Connection> connect();

    List<Connection> getConnections();

    CompletableFuture<Connection> connect(Consumer<Connection> consumer);
}
