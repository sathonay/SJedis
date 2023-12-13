package com.sjedis.client.api.implementations;

import com.sjedis.client.api.SJedis;
import com.sjedis.client.api.Connection;
import com.sjedis.client.api.models.Response;
import com.sjedis.common.map.PacketHandlerMap;
import com.sjedis.common.map.PacketHandlers;
import com.sjedis.common.packet.PacketRegistry;
import com.sjedis.common.packet.PasswordPacket;
import com.sjedis.common.packet.ResponsePacket;
import com.sjedis.common.util.CompletableFutureUtil;
import lombok.Builder;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Builder
public class SimpleSJedis implements SJedis {


    private final String host;
    private final int port;
    private final String password;

    private final PacketHandlers<APIConnection> packetHandlers = new PacketHandlers<>();

    private final List<Connection> connections = new ArrayList<>();

    public SimpleSJedis(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        initPacketHandler();
    }

    public void initPacketHandler() {
        packetHandlers.setHandler(ResponsePacket.class, (connection, packet) -> {
            connection.getCompletableFuture(packet.requestID).ifPresent(responseCompletableFuture ->
                    ForkJoinPool.commonPool().execute(() -> responseCompletableFuture.complete(new Response(connection, packet.response)))
            );
        });
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }

    @Override
    public CompletableFuture<Connection> connect() {
        return CompletableFutureUtil.future(this::buildConnection);
    }

    private Connection buildConnection() throws IOException {
        APIConnection connection = new APIConnection(buildSocket(), packetHandlers);
        connection.send(new PasswordPacket(password));
        return connection;
    }

    private Socket buildSocket() throws IOException {
            Socket socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            return socket;
    }

    public static class SimpleSJedisBuilder implements SJedis.SJedisBuilder {}
}
