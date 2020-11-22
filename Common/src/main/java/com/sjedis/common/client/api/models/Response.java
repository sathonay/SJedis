package com.sjedis.common.client.api.models;

import com.sjedis.common.client.api.Connection;
import lombok.Getter;


@Getter
public class Response extends com.sjedis.common.response.Response {
    private final Connection connection;

    public Response(Connection connection, com.sjedis.common.response.Response response) {
        super(response.toMap());
        this.connection = connection;
    }
}
