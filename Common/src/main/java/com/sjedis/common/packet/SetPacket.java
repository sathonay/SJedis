package com.sjedis.common.packet;

import java.util.Map;

public class SetPacket extends Packet {

    public final String[] keys;
    public final Object[] values;

    public SetPacket(String[] keys, Object[] values) {
        super((byte) PacketRegistry.SET_PACKET.ordinal());
        this.keys = keys;
        this.values = values;
    }
}
