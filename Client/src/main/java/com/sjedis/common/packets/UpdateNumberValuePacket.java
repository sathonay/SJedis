package com.sjedis.common.packets;

import java.io.Serializable;

public class UpdateNumberValuePacket implements Serializable {

    public String key;
    public double value;

    public UpdateNumberValuePacket(String key, double value) {
        this.key = key;
        this.value = value;
    }
}
