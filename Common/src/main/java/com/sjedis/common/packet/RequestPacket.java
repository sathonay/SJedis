package com.sjedis.common.packet;

import java.util.UUID;

public class RequestPacket extends Packet{

    public final UUID requestID;
    public final String[] keys;

    public RequestPacket(UUID requestID, String[] keys) {
        this.requestID = requestID;
        this.keys = keys;
    }
}
