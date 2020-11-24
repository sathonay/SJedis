package com.sjedis.client.api.models;

import java.util.ArrayList;
import java.util.List;

public class Multi<T> {

    private List<T> typeList = new ArrayList<>();

    public Multi add(T t) {
        typeList.add(t);
        return this;
    }

    public List<T> toList() {
        return typeList;
    }
}