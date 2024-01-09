# SJedis

Key-Value hover TCP/IP sockets

Requirements
-

To build SJedis, the following will need to be installed and available from your shell:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Git](https://git-scm.com)
* [Maven](https://maven.apache.org)

How to
-

### Run SJedis-server

```shell
java -jar SJedis.jar <port> <password> 
```

### Use SJedis Client

```java
        SJedis jedis = SJedis.builder()
                .host("127.0.0.1")
                .port(1234)
                .password("1234").build();

        jedis.connect().thenCompose(connection -> {
            connection.set(
                    new String[]{"hello", "salut"}
                    new Object[]{"salut", "hello"}
            );
            return connection.get(
                    "hello",
                    "salut",
                    "holla"
            );
        }).thenAccept(response -> {
            System.out.println(Arrays.toString(response.toArray()));
            response.getConnection().close();
        }).whenComplete((unused, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
        }).join();
```
