package com.pgssoft;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;

public class HttpClientVerifyTest {

    @Test
    public void shouldHandleAllHttpMethods() throws Exception {

        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).DELETE().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("HEAD", noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("OPTIONS", noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("PATCH", noBody()).build(), discarding());

        httpClientMock.verify().get("http://localhost").called();
        httpClientMock.verify().post("http://localhost").called();
        httpClientMock.verify().delete("http://localhost").called();
        httpClientMock.verify().put("http://localhost").called();
        httpClientMock.verify().options("http://localhost").called();
        httpClientMock.verify().head("http://localhost").called();
        httpClientMock.verify().patch("http://localhost").called();
    }
}
