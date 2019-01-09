package com.pgssoft;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.pgssoft.matchers.HttpResponseMatchers.hasContent;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public class HttpClientMockBuilderTest {

    @Test
    public void shouldMatchSeparateHostAndPath() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("http://localhost")
                .withPath("/login")
                .doReturnStatus(200);

        var req = HttpRequest.newBuilder(URI.create("http://localhost/login"))
                .POST(noBody())
                .build();
        var res = httpClientMock.send(req, ofString());

        assertThat(res.statusCode(), equalTo(200));
    }

    @Test
    public void shouldMatchSeparatePathAndParameter() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost()
                .withPath("/login")
                .withParameter("a", "1")
                .doReturn("one");
        httpClientMock.onPost()
                .withPath("/login")
                .withParameter("b", "2")
                .doReturn("two");

        var firstResponse = httpClientMock.send(
                HttpRequest.newBuilder(URI.create("http://localhost/login?a=1"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        var secondResponse = httpClientMock.send(
                HttpRequest.newBuilder(URI.create("http://localhost/login?b=2"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        var thirdResponse = httpClientMock.send(
                HttpRequest.newBuilder(URI.create("http://localhost/login?a=1&b=2"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        assertThat(firstResponse, hasContent("one"));
        assertThat(secondResponse, hasContent("two"));
        assertNull(thirdResponse);
    }
}
