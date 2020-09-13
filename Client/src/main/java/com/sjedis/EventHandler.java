package com.sjedis;

import com.sjedis.client.event.PacketHandleEvent;
import com.sjedis.common.packets.KeyValuePacket;

public class EventHandler extends PacketHandleEvent {

    @Override
    public void handleObjectEvent(Object object) {
        System.out.println("salut");
        if (object instanceof TestObject) {
            TestObject testObject = (TestObject) object;
            System.out.println(testObject.string);
        }
    }

    @Override
    public void handleKeyValuePacketEvent(KeyValuePacket packet) {
        System.out.println("key: " + packet.key + ", value: " + packet.value);
    }
}
