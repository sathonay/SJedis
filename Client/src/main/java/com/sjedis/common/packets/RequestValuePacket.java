package com.sjedis.common.packets;

import java.io.Serializable;

public class RequestValuePacket implements Serializable {

    public final String requestedValue;

    public RequestValuePacket(String requestedValue) {
        this.requestedValue = requestedValue;
    }
}
