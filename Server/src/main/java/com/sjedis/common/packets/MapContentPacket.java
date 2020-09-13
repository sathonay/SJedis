package com.sjedis.common.packets;

import java.io.Serializable;
import java.util.Map;

public class MapContentPacket implements Serializable {
    public final Map<String, Object> map;

    public MapContentPacket(Map<String, Object> map) {
        this.map = map;
    }
}
