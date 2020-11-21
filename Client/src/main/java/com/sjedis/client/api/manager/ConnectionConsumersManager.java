package com.sjedis.client.api.manager;

import com.sjedis.client.api.builders.ConnectionBuilder;
import com.sjedis.common.packet.ResponsePacket;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConnectionConsumersManager {
    private static final Map<Class, Consumer> CONSUMERS;

    static {
        CONSUMERS = new HashMap<>();
        CONSUMERS.put(ResponsePacket.class, (Consumer<Pair<ConnectionBuilder, ResponsePacket>>) pair -> {
            ConnectionBuilder connection = pair.getKey();
            ResponsePacket packet = pair.getValue();
            if (connection.getFutureMap().containsKey(packet.requestID))
                connection.getFutureMap().get(packet.requestID).complete(packet.response);
        });
    }

    public static Consumer getConsumer(Class aClass) {
        return CONSUMERS.get(aClass);
    }
}
