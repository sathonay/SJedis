package com.sjedis.client.api;

import com.sjedis.client.api.implementation.sjedis.SimpleSJedis;
import com.sjedis.common.client.api.Connection;

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
