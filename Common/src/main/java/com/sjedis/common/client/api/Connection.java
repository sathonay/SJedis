package com.sjedis.common.client.api;

import com.sjedis.common.client.api.models.Multi;
import com.sjedis.common.client.api.models.PreparedSet;
import com.sjedis.common.client.api.models.Response;
import com.sjedis.common.packet.Packet;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Connection extends com.sjedis.common.connection.Connection {

    void send(Packet... packets);

    void set(String key, Object value);

    void set(Multi<String> keys, Object value);

    void set(Map<String, Object> map);

    void set(PreparedSet preparedSet);

    CompletableFuture<Response> get(String... keys);

    //CompletableFuture<Response> get(Consumer<Response> supplier, String... keys);

    Optional<CompletableFuture<Response>> getCompletableFuture(UUID uuid);

    void close();
}
