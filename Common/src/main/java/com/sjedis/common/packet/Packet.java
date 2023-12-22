package com.sjedis.common.packet;

import java.io.Serializable;

public class Packet implements Serializable {
    public final byte id;

    public Packet(byte id) {
        this.id = id;
    }
}
