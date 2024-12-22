package com.sjedis.client.api;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
            SJedis jedis = SJedis.builder()
            .host("127.0.0.1")
            .port(12345)
            .password("test").build();

        jedis.connect().thenCompose(connection -> {
            connection.set(
                    new String[]{"hello", "salut"},
                    new Object[]{"salut", "hello"}
            );
            return connection.get(
                    "hello",
                    "salut",
                    "holla"
            );
        }).thenAccept(response -> {
            System.out.println(Arrays.toString(response.toArray()));
            response.getConnection().close();
        }).whenComplete((unused, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
        }).join();
    }
}
