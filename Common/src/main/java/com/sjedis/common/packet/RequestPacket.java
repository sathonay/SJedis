package com.sjedis.common.packet;

import java.util.UUID;

public class RequestPacket extends Packet {

    public final UUID requestID;
    public final String[] keys;

    public RequestPacket(UUID requestID, String[] keys) {
        super((byte) PacketRegistry.REQUEST_PACKET.ordinal());
        this.requestID = requestID;
        this.keys = keys;
    }
}
