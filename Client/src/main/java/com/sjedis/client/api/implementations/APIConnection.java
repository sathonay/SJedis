package com.sjedis.client.api.implementations;

import com.sjedis.client.api.Connection;
import com.sjedis.client.api.models.Response;
import com.sjedis.common.connection.implementations.PacketConnection;
import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.packet.Packet;
import com.sjedis.common.packet.RequestPacket;
import com.sjedis.common.packet.SetPacket;
import com.sjedis.common.packet.handler.PacketHandler;
import com.sjedis.common.packet.handler.PacketHandlers;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class APIConnection extends PacketConnection implements Connection {

    private final Map<UUID, CompletableFuture<Response>> futureResponseMap = new HashMap<>();

    public APIConnection(Socket socket, PacketHandlers packetHandlers) {
        super(socket, packetHandlers);
    }

    @Override
    public void send(Packet... packets) {
        for (Packet packet : packets) {
            sendSerializable(packet);
        }
    }

    @Override
    public void set(String key, Object value) {
        set(new String[]{key}, new Object[]{value});
    }

    @Override
    public void set(String[] keys, Object[] objects) {
        if (keys.length != objects.length) {
            try {
                throw new IOException("Keys and Objects size missmatch");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        set(keys, objects);
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

    @Override
    public boolean isClosed() {
        return false;
    }
}
