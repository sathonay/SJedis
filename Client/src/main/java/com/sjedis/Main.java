package com.sjedis;

import com.sjedis.client.SJedis;
import com.sjedis.client.builder.SJedisBuilder;


public class Main {
    public static void main(String[] args) {
        try {
            SJedis sJedis = new SJedisBuilder()
                    .setSJedisIp("51.178.45.131")
                    .setSJedisPort(15342)
                    .setSJedisPassword("ksAfKoPlKPliguNunHKKuvyjndkhfgmISEKJHGMIgjsdh4564654")
                    .build();

            SJedis sJedis1 = new SJedisBuilder()
                    .setSJedisIp("51.178.45.131")
                    .setSJedisPort(15342)
                    .setSJedisPassword("ksAfKoPlKPliguNunHKKuvyjndkhfgmISEKJHGMIgjsdh4564654")
                    .build();

            if (sJedis1.connect()) {
                sJedis1.addHandler(new EventHandler());
            }

            if (sJedis.connect()) {

                sJedis.updateNumber("online", 0.2);

                /*for (int i = 0; i < 10; i++) {
                    long start = System.currentTimeMillis();
                    sJedis.set("salut" + i, i * 2);
                    System.out.println(sJedis.get("salut" + i));
                    System.out.println("delta " + (System.currentTimeMillis() - start));
                }*/

                //sJedis.set("salut", "salut");

                //sJedis.getCache().forEach((s, o) -> System.out.println(s + "/" + o));

                sJedis.close();
            }
            //sJedis1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
