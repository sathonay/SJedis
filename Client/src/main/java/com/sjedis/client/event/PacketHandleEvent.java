package com.sjedis.client.event;

import com.sjedis.common.packets.KeyValuePacket;

public abstract class PacketHandleEvent {

    public void handleObjectEvent (Object object) {}

    public void handleKeyValuePacketEvent(KeyValuePacket packet) {}
}
