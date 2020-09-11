package com.sjedis;

import com.sjedis.client.SJedis;
import com.sjedis.client.builder.SJedisBuilder;


public class Main {
    public static void main(String[] args) {
        SJedis sJedis = new SJedisBuilder()
                .setSJedisIp("51.178.45.131")
                .setSJedisPort(15342)
                .build();

        if (sJedis.connect()) {



            sJedis.set("salut", null);
            System.out.println(sJedis.get("salut"));


            sJedis.close();
        }
    }
}
