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
            && conf.contains("port") 
            && conf.contains("password")
        ) {
            args = new String[]{conf.getString("port"), conf.getString("password")};
        }

        if (args.length < 2) {
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
