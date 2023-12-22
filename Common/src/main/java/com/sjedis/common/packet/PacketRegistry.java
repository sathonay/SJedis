package com.sjedis.common.packet;

import lombok.Getter;

public enum PacketRegistry {

    PASSWORD_PACKET(PasswordPacket.class),
    SET_PACKET(SetPacket.class),
    REQUEST_PACKET(RequestPacket.class),
    RESPONSE_PACKET(ResponsePacket.class),
    ;

    @Getter
    private Class<? extends Packet> packetClass;

    PacketRegistry(Class<? extends Packet> clazz) {
        this.packetClass = clazz;
    }
}
