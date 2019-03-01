package com.pgssoft.httpclient;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class HttpClientMockAsyncTest {

    @Test
    void sendAsync_Should_ReturnCompletedFuture() throws ExecutionException, InterruptedException {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("localhost")
                .withPath("/login")
                .doReturn(200, "ABC");

        var req = newBuilder(URI.create("http://localhost/login"))
                .POST(noBody())
                .build();
        var res = httpClientMock.sendAsync(req, HttpResponse.BodyHandlers.ofString());

        assertThat(res.get().body(), equalTo("ABC"));
        assertThat(res.get().statusCode(), equalTo(200));
        httpClientMock.verify().post("http://localhost/login");
    }


}
