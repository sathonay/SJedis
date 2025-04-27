package com.sjedis.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigFile {
    
    private File file;
    public final Map<String, String> values = new HashMap<>();

    public ConfigFile(File file)
    {
        this.file = file;
    }

    public ConfigFile readFile()
    {
        if (!file.exists())
        {
            System.out.println("Unable to read config file (it doesn't exist): " + file.getAbsolutePath());
            return this;
        }

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                String[] splits = scan.nextLine().split(": ", 2);
                if (splits.length == 2)
                    values.put(splits[0], splits[1]);
            }

            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean exist() {
        return file.exists();
    }

    public String getString(String key)
    {
        return (String) values.get(key);
    }

    //TODO on first get parse and then save in map (maybe)
    /*public int getInt(String key)
    {
        return Integer.parseInt((String) values.get(key));
    }*/
    // currenly useless

    public boolean contains(String key)
    {
        return values.containsKey(key);
    }
}
