package com.sjedis;

import com.sjedis.server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing password java -jar <file> <password>");
            System.exit(0);
        }
        new Server(args);
    }
}
