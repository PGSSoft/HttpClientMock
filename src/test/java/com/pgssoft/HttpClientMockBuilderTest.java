package com.pgssoft;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClientMockBuilderTest {

    @Test
    public void shouldMatchSeparateHostAndPath() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("http://localhost")
                .withPath("/login")
                .doReturnStatus(200);

        var req = HttpRequest.newBuilder(URI.create("http://localhost/login"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var res = httpClientMock.send(req, HttpResponse.BodyHandlers.ofString());

        assertThat(res.statusCode(), equalTo(200));
    }
}
