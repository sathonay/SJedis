# SJedis

That's something ¯\_(ツ)_/¯

How to
-

### Run SJedis-server

```shell
java -jar <file> <password> <port>
```

### Build SJedis

```java
        try {
            SJedis sJedis = new SJedisBuilder()
                    .setSJedisIp(server_ip)
                    .setSJedisPort(server_port)
                    .setSJedisPassword(server_password)
                    .build(); // Throws an error if the password or the ip equals to null
        } catch (Exception e) {
            e.printStackTrace();
        }
```

### Use SJedis

```java
            if (sJedis.connect()) { // true if the connection was established

                PreparedSet preparedSet = new PreparedSetBuilder()
                        .set("hello", "world")
                        .set("sathonay_balance", 1000)
                        .build();
                sJedis.send(preparedSet); // send multiple values to the server

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("hello", "world");
                map.put("sathonay_balance", 1000);
                sJedis.send(map); // send multiple values to the server

                sJedis.send("sathonay_balance", 500); // sends value to the server

                sJedis.updateNumber("sathonay_balance", -250); // all calculations are server side
                
                sJedis.get("hello"); // return the value or null

                sJedis.getOrDefault("hello", "France"); // return the value or default value
                
                sJedis.close(); // close the connection at the end of the programme
            }
```
