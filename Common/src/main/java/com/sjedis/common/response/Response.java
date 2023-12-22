package com.sjedis.common.response;

import java.io.Serializable;
import java.util.Optional;

public class Response implements Serializable {

    private final Object[] data;

    public Response(Object[] data) {
        this.data = data;
    }

    public <T> T get(int index) {
        return index <= (data.length - 1) ?  (T) data[index] : null;
    }

    public <T> Optional<T> getOptional(int index) {
        return Optional.ofNullable(get(index));
    }

    public <T> T[] toArray() {
        return (T[]) data.clone();
    }
}
