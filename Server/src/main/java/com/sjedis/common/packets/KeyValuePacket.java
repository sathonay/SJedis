package com.sjedis.common.packets;

import java.io.Serializable;

public class KeyValuePacket implements Serializable {

    public final String key;
    public final Object value;

    public KeyValuePacket(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}

