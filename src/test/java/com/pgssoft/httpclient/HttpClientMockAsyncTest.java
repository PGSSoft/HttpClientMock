package com.pgssoft.httpclient;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class HttpClientMockAsyncTest {

    private static final String EXAMPLE_URI = "http://localhost/login";

    @Test
    void sendAsync_Should_ReturnCompletedFuture() throws ExecutionException, InterruptedException {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("localhost")
                .withPath("/login")
                .doReturn(200, "ABC");

        var req = newBuilder(URI.create(EXAMPLE_URI))
                .POST(noBody())
                .build();
        var res = httpClientMock.sendAsync(req, HttpResponse.BodyHandlers.ofString());

        assertThat(res.get().body(), equalTo("ABC"));
        assertThat(res.get().statusCode(), equalTo(200));
        httpClientMock.verify().post(EXAMPLE_URI);
    }

    @Test
    void sendAsync_Should_ReturnTheConfiguredExceptionInTheCompletedFuture() {
        HttpClientMock httpClientMock = new HttpClientMock();
        var expectedException = new IOException("expected exception");

        httpClientMock.onGet(EXAMPLE_URI).doThrowException(expectedException);

        var req = newBuilder(URI.create(EXAMPLE_URI)).GET().build();
        var res = httpClientMock.sendAsync(req, HttpResponse.BodyHandlers.ofString());

        assertThat(res.isCompletedExceptionally(), is(true));
        try {
            res.get();
        } catch (Exception e) {
            assertThat(e.getCause(), equalTo(expectedException));
        }
    }

}
