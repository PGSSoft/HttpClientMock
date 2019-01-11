package com.pgssoft;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

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

    @Test
    public void shouldCountNumberOfHttpMethodCalls() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), discarding());

        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(noBody()).build(), discarding());

        httpClientMock.send(newBuilder(URI.create("http://localhost")).DELETE().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).DELETE().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).DELETE().build(), discarding());

        httpClientMock.verify().get("http://localhost").called();
        httpClientMock.verify().post("http://localhost").called(2);
        httpClientMock.verify().delete("http://localhost").called(3);

        httpClientMock.verify().get().called(greaterThanOrEqualTo(1));
        httpClientMock.verify().post().called(greaterThanOrEqualTo(1));
        httpClientMock.verify().delete().called(greaterThanOrEqualTo(1));
    }

    @Test
    public void shouldCountNumberOfUrlCalls() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), discarding());

        httpClientMock.send(newBuilder(URI.create("http://www.google.com")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://www.google.com")).GET().build(), discarding());

        httpClientMock.send(newBuilder(URI.create("http://example.com")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://example.com")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://example.com")).GET().build(), discarding());

        httpClientMock.verify().get("http://localhost").called();
        httpClientMock.verify().get("http://www.google.com").called(2);
        httpClientMock.verify().get("http://example.com").called(3);
    }
}
