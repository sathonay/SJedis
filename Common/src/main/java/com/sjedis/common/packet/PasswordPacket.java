package com.sjedis.common.packet;

public class PasswordPacket extends Packet {

    public final String password;

    public PasswordPacket(String password) {
        super((byte) PacketRegistry.PASSWORD_PACKET.ordinal());
        this.password = password;
    }
}
