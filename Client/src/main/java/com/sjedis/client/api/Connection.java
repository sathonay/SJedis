package com.sjedis.client.api;

import com.sjedis.client.api.models.Response;
import com.sjedis.common.packet.Packet;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Connection extends com.sjedis.common.connection.AESConnection {

    void set(String key, Object value);

    void set(String[] keys, Object[] objects);

    CompletableFuture<Response> get(String... keys);

    Optional<CompletableFuture<Response>> getCompletableFuture(UUID uuid);

    void close();
}
