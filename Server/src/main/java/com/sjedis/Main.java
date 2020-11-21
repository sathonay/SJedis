package com.sjedis;

import com.sjedis.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Port missing: java -jar <file> <port> <password>");
            return;
        }

        String password = null;

        Integer port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            System.err.println("The port is not a valid number");
            return;
        }

        if (args.length > 1) {
            password = args[1];
        }

        new Server(port, password);
    }
}
