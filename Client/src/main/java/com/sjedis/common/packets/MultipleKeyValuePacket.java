package com.sjedis.common.packets;

import java.io.Serializable;
import java.util.Map;

public class MultipleKeyValuePacket implements Serializable {

    public final Map<String, Object> value;

    public MultipleKeyValuePacket(Map<String, Object> value) {
        this.value = value;
    }
}
