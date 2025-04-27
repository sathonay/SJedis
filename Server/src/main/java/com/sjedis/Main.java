package com.sjedis;

import java.io.File;

import com.sjedis.server.ConfigFile;
import com.sjedis.server.Server;

public class Main {

    public static void main(String[] args) {
        // TODO make aes configurable ? (enable & algo options) ??
        ConfigFile conf = new ConfigFile(
            new File("server.conf")
        ).readFile();
        
        if (args.length < 2
                && conf != null
                && conf.contains("port")
                && conf.contains("password")
        ) {
            args = new String[]{conf.getString("port"), conf.getString("password")};
        }

        if (args.length < 2) {
            System.err.println("Port missing: java -jar <file> <port> <password>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            String password = args[1];
            new Server(port, password);
        } catch (NumberFormatException exception) {
            System.err.println("The port is not a valid number");
        }
    }
}
