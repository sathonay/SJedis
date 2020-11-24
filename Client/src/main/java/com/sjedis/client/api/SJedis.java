package com.sjedis.client.api;

import com.sjedis.client.api.implementations.sjedis.SimpleSJedis;
import com.sjedis.client.api.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SJedis {

    interface SJedisBuilder {
        SJedisBuilder host(String host);
        SJedisBuilder port(int port);
        SJedisBuilder password(String password);

        SJedis build();
    }

    CompletableFuture<Connection> connect();

    List<Connection> getConnections();

    static SJedisBuilder builder() {
        return SimpleSJedis.builder();
    }
}
