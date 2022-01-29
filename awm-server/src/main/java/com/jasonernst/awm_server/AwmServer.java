package com.jasonernst.awm_server;

import com.sun.net.httpserver.HttpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AwmServer {
    private static Logger logger = LoggerFactory.getLogger(AwmServer.class);
    public static final int DEFAULT_PORT = 8000;
    public static final int DEFAULT_MYSQL_PORT = 3000;
    private static final int THREAD_MAX = 10;
    private HttpServer server;
    private MySqlPersistence mySqlPersistence;

    // https://dzone.com/articles/simple-http-server-in-java
    public AwmServer(int port) throws IOException {
        logger.debug("Test");
        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        mySqlPersistence = new MySqlPersistence("localhost", 3000, "default", "password");
        mySqlPersistence.connect();
        server.createContext("/", new AwmHttpHandler(mySqlPersistence));
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_MAX);
        server.setExecutor(threadPoolExecutor);
        server.start();
        logger.debug("Listening on port: {}", port);
    }

    public static void main(String args[]) throws IOException {
        AwmServer awmServer = new AwmServer(DEFAULT_PORT);
    }
}