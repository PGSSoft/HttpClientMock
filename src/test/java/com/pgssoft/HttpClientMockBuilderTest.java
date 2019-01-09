package com.pgssoft;

import com.pgssoft.condition.Condition;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static com.pgssoft.matchers.HttpResponseMatchers.hasContent;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
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

        var req = newBuilder(URI.create("http://localhost/login"))
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
                newBuilder(URI.create("http://localhost/login?a=1"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        var secondResponse = httpClientMock.send(
                newBuilder(URI.create("http://localhost/login?b=2"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        var thirdResponse = httpClientMock.send(
                newBuilder(URI.create("http://localhost/login?a=1&b=2"))
                        .POST(noBody())
                        .build(),
                ofString()
        );

        assertThat(firstResponse, hasContent("one"));
        assertThat(secondResponse, hasContent("two"));
        assertNull(thirdResponse);
    }

    @Test
    public void shouldUseRightMethod() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo").doReturn("get");
        httpClientMock.onPost("/foo").doReturn("post");
        httpClientMock.onPut("/foo").doReturn("put");
        httpClientMock.onDelete("/foo").doReturn("delete");
        httpClientMock.onHead("/foo").doReturn("head");
        httpClientMock.onOptions("/foo").doReturn("options");
        httpClientMock.onPatch("/foo").doReturn("patch");

        var getResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        var postResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).POST(noBody()).build(), ofString());
        var putResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).PUT(noBody()).build(), ofString());
        var deleteResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).DELETE().build(), ofString());
        var headResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("HEAD", noBody()).build(), ofString());
        var optionsResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("OPTIONS", noBody()).build(), ofString());
        var patchResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("PATCH", noBody()).build(), ofString());

        assertThat(getResponse, hasContent("get"));
        assertThat(postResponse, hasContent("post"));
        assertThat(putResponse, hasContent("put"));
        assertThat(deleteResponse, hasContent("delete"));
        assertThat(headResponse, hasContent("head"));
        assertThat(optionsResponse, hasContent("options"));
        assertThat(patchResponse, hasContent("patch"));
    }

    @Test
    public void shouldCheckCustomRule() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        final Condition fooCondition = request -> request.uri().toString().contains("foo");
        httpClientMock.onGet("http://localhost/foo/bar")
                .with(fooCondition)
                .doReturn("yes");

        final HttpResponse first = httpClientMock.send(newBuilder(URI.create("http://localhost/foo/bar")).GET().build(), ofString());

        assertThat(first, hasContent("yes"));
    }

    @Test
    public void shouldUseRightHostAndPath() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onGet("http://localhost:8080/foo").doReturn("localhost");
        httpClientMock.onGet("http://www.google.com").doReturn("google");
        httpClientMock.onGet("https://www.google.com").doReturn("https");

        final HttpResponse localhost = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), ofString());
        final HttpResponse google = httpClientMock.send(newBuilder(URI.create("http://www.google.com")).GET().build(), ofString());
        final HttpResponse https = httpClientMock.send(newBuilder(URI.create("https://www.google.com")).GET().build(), ofString());

        assertThat(localhost, hasContent("localhost"));
        assertThat(google, hasContent("google"));
        assertThat(https, hasContent("https"));
    }

    @Test
    public void shouldMatchRightHeaderValue() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/login").withHeader("User-Agent", "Mozilla")
                .doReturn("mozilla");
        httpClientMock
                .onGet("/login").withHeader("User-Agent", "Chrome")
                .doReturn("chrome");

        final var getMozilla = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().header("User-Agent", "Mozilla").build(), ofString());
        final var getChrome = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().header("User-Agent", "Chrome").build(), ofString());
        //final var getSafari = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().header("User-Agent", "Safari").build(), ofString());

        assertThat(getMozilla, hasContent("mozilla"));
        assertThat(getChrome, hasContent("chrome"));
        //assertThat(httpClientMock.execute(getSafari), hasStatus(404));
    }
}
