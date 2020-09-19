package com.sjedis;

import com.sjedis.server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing password or port java -jar <file> <password> <port>");
            System.exit(0);
            return;
        }
        new Server(args[0], Integer.parseInt(args[1]));
    }
}
