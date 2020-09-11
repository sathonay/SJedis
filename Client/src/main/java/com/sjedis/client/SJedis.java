package com.sjedis.client;

public interface SJedis {

    boolean connect();

    void set(String key, Object value);

    Object get(String key);

    boolean close();

}
