package com.sjedis.common.packet;

import java.util.Map;

public class SetPacket extends Packet {

    public final Map<String, Object> map;

    public SetPacket(Map<String, Object> map) {
        super((byte) PacketRegistry.SET_PACKET.ordinal());
        this.map = map;
    }
}
