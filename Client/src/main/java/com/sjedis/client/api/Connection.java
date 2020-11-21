package com.sjedis.client.api;

import com.sjedis.common.packet.Packet;
import com.sjedis.common.response.Response;
import com.sjedis.client.api.models.Multi;
import com.sjedis.client.api.models.PreparedSet;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Connection {

    void send(Packet... packets);

    void set(String key, Object value);

    void set(Multi<String> keys, Object value);

    void set(Map<String, Object> map);

    void set(PreparedSet preparedSet);

    CompletableFuture<Response> get(String... keys);

    CompletableFuture<Response> get(Consumer<Response> supplier, String... keys);

    void close();
}
