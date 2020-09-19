package com.sjedis.client.event;

import com.sjedis.common.packets.KeyValuePacket;
import com.sjedis.common.packets.MultipleKeyValuePacket;

public abstract class PacketHandleEvent {

    public void handleKeyValuePacketEvent(KeyValuePacket packet) {}

    public void handleMultipleKeyValuePacketEvent(MultipleKeyValuePacket packet) {}
}
