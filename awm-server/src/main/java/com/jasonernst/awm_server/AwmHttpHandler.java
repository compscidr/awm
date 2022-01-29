package com.jasonernst.awm_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AwmHttpHandler implements HttpHandler {

    private static Logger logger = LoggerFactory.getLogger(AwmHttpHandler.class);
    private Persistence persistence;

    public AwmHttpHandler(Persistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue = null;
        if ("GET".equals(httpExchange.getRequestMethod())) {
            requestParamValue = handleGetRequest(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            requestParamValue = handlePostRequest(httpExchange);
        }
        handleResponse(httpExchange,requestParamValue);
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        logger.debug("Got GET request");
        return httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
    }

    private String handlePostRequest(HttpExchange httpExchange) {
        logger.debug("Got POST request");
        return "";
    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
        logger.debug("Handling response");
    }
}
