package com.sjedis.common.map;

import com.sjedis.common.connection.Connection;
import com.sjedis.common.packet.Packet;

public interface PacketHandler<C extends Connection, P extends Packet> {

   void handle(C connection, P packet);
}
