package com.sjedis.server.client;

import com.sjedis.common.packet.PasswordPacket;
import com.sjedis.common.packet.RequestPacket;
import com.sjedis.common.packet.ResponsePacket;
import com.sjedis.common.packet.SetPacket;
import com.sjedis.common.response.Response;
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

        CONSUMERS.put(SetPacket.class, (Consumer<Pair<ClientConnection, SetPacket>>) pair -> Server.getINSTANCE().getCache().putAll(pair.getValue().map));

        CONSUMERS.put(RequestPacket.class, (Consumer<Pair<ClientConnection, RequestPacket>>) pair -> {
            ClientConnection connection = pair.getKey();
            RequestPacket packet = pair.getValue();;
            Map<String, Object> responseMap = new HashMap<>();
            Map<String, Object> cache = Server.getINSTANCE().getCache();
            for (String key : packet.keys) responseMap.put(key, cache.get(key));
            connection.send(new ResponsePacket(packet.requestID, new Response(responseMap)));
        });
    }

    public static Consumer getConsumer(Class aClass) {
        return CONSUMERS.get(aClass);
    }
}
