package com.sjedis.client.api.implementations.connection;

import com.sjedis.client.api.Connection;
import com.sjedis.client.api.models.Multi;
import com.sjedis.client.api.models.PreparedSet;
import com.sjedis.client.api.models.Response;
import com.sjedis.common.connection.implementations.PacketConnection;
import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.RequestPacket;
import com.sjedis.common.packet.SetPacket;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class APIConnection extends PacketConnection implements Connection {

    private final Map<UUID, CompletableFuture<Response>> futureResponseMap = new HashMap<>();

    public APIConnection(Socket socket, PacketHandlerMap handlerMap) {
        super(socket, handlerMap);
    }

    @Override
    public void send(Packet... packets) {
        for (Packet packet : packets) {
            sendSerializable(packet);
        }
    }

    @Override
    public void set(String key, Object value) {
        set(Collections.singletonMap(key, value));
    }

    @Override
    public void set(Multi<String> keys, Object value) {
        set(new PreparedSet().set(keys, value));
    }

    @Override
    public void set(Map<String, Object> map) {
        send(new SetPacket(map));
    }

    @Override
    public void set(PreparedSet preparedSet) {
        set(preparedSet.toMap());
    }

    @Override
    public CompletableFuture<Response> get(String... keys) {
        return buildRequest(UUID.randomUUID(), keys);
    }

    private CompletableFuture<Response> buildRequest(UUID requestID, String... keys) {
        CompletableFuture<Response> future = futureResponseMap.compute(requestID, (uuid, completableFuture) -> new CompletableFuture<>());
        send(new RequestPacket(requestID, keys));
        return future;
    }

    @Override
    public Optional<CompletableFuture<Response>> getCompletableFuture(UUID uuid) {
        return Optional.ofNullable(futureResponseMap.get(uuid));
    }

    @Override
    public void close() {
        futureResponseMap.values().forEach(future -> future.cancel(false));
        futureResponseMap.clear();
        super.close();
    }
}
