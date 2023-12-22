package com.sjedis.common.packet;

import com.sjedis.common.response.Response;

import java.util.UUID;

public class ResponsePacket extends Packet {

    public final UUID requestID;
    public final Response response;

    public ResponsePacket(UUID requestID, Response response) {
        super((byte) PacketRegistry.RESPONSE_PACKET.ordinal());
        this.requestID = requestID;
        this.response = response;
    }
}
