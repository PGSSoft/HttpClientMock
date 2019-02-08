package com.pgssoft.httpclient;

import java.net.URI;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;

public class TestRequests {

    static public HttpRequest post(String url) {
        return newBuilder(URI.create(url)).POST(noBody()).build();
    }

    static public HttpRequest get(String url) {
        return newBuilder(URI.create(url)).GET().build();
    }
}
