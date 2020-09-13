package com.sjedis.common.packets;

import java.io.Serializable;

public class AuthSuccessPacket implements Serializable {

    public final boolean valid;

    public AuthSuccessPacket(boolean valid) {
        this.valid = valid;
    }
}
