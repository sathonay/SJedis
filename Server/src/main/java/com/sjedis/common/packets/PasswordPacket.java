package com.sjedis.common.packets;

import java.io.Serializable;

public class PasswordPacket implements Serializable {

    public final String password;

    public PasswordPacket(String password) {
        this.password = password;
    }
}
