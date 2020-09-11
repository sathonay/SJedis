package com.sjedis.server.threads;

public class ShutdownThread extends Thread {
    @Override
    public void run() {
        System.out.println("SJedis server stopped!");
    }
}
