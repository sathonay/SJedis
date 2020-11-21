package com.sjedis.server.client;

import com.sathonay.common.packet.PasswordPacket;
import com.sjedis.server.Server;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClientConsumers {
    private static final Map<Class, Consumer> CONSUMERS;

    static {
        CONSUMERS = new HashMap<>();
        CONSUMERS.put(PasswordPacket.class, (Consumer<Pair<ClientConnection, PasswordPacket>>) pair -> {
            ClientConnection connection = pair.getKey();
            if (connection.isLogin()) return;
            Server server = Server.getINSTANCE();
            if (!connection.isLogin()
                    && (server.getPassword() == null || server.getPassword().equals(pair.getValue().password))) connection.setLogin(true);
        });
    }

    public static Consumer getConsumer(Class aClass) {
        return CONSUMERS.get(aClass);
    }
}
