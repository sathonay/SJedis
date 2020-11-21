package com.sjedis;

import com.sjedis.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Port missing: java -jar <file> <port> <password>");
            return;
        }

        String password = args.length > 1 ? args[1] : null;

        try {
            new Server(Integer.parseInt(args[0]), password);
        } catch (NumberFormatException exception) {
            System.err.println("The port is not a valid number");
        }
    }
}
