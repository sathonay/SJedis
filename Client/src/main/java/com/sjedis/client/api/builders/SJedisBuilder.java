package com.sjedis.client.api.builders;

import com.sjedis.client.api.Connection;
import com.sjedis.client.api.SJedis;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Data
public class SJedisBuilder {

    private String host;
    private int port;
    private String password;

    public SJedisBuilder(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SJedisBuilder(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public SJedis build() {
        return new SJedis() {

            @Getter @Setter
            private String host = SJedisBuilder.this.host;
            @Getter @Setter
            private int port = SJedisBuilder.this.port;
            @Getter @Setter
            private String password = SJedisBuilder.this.password;

            private final List<Connection> connections = new ArrayList<>();

            public List<Connection> getConnections() {
                return new ArrayList<>(connections);
            }

            @Override
            public CompletableFuture<Connection> connect(Consumer<Connection> consumer) {
                CompletableFuture<Connection> future = future(consumer);
                future.complete(buildConnection());
                return future;
            }

            @Override
            public Optional<Connection> connect() {
                return Optional.ofNullable(buildConnection());
            }

            private Connection buildConnection() {
                Socket socket = buildSocket();
                return socket == null ? null : new ConnectionBuilder(socket, password, connections);
            }

            private <T> CompletableFuture<T> future(Consumer<T> consumer) {
                CompletableFuture<T> future = new CompletableFuture<>();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            consumer.accept(future.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        stop();
                    }
                };
                return future;
            }

            private Socket buildSocket() {
                try {
                    Socket socket = new Socket(host, port);
                    socket.setTcpNoDelay(true);
                    return socket;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}
