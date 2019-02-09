package com.pgssoft.httpclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class HttpClientVerifyTest {

    @Test
    void shouldHandleAllHttpMethodsWithURL() throws Exception {

        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost").doReturn("empty");
        httpClientMock.onPost("http://localhost").doReturn("empty");
        httpClientMock.onPut("http://localhost").doReturn("empty");
        httpClientMock.onDelete("http://localhost").doReturn("empty");
        httpClientMock.onHead("http://localhost").doReturn("empty");
        httpClientMock.onOptions("http://localhost").doReturn("empty");
        httpClientMock.onPatch("http://localhost").doReturn("empty");

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
    void shouldHandleAllHttpMethodsWithoutURL() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost").doReturn("empty");
        httpClientMock.onPost("http://localhost").doReturn("empty");
        httpClientMock.onPut("http://localhost").doReturn("empty");
        httpClientMock.onDelete("http://localhost").doReturn("empty");
        httpClientMock.onHead("http://localhost").doReturn("empty");
        httpClientMock.onOptions("http://localhost").doReturn("empty");
        httpClientMock.onPatch("http://localhost").doReturn("empty");

        httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).DELETE().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("HEAD", noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("OPTIONS", noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).method("PATCH", noBody()).build(), discarding());

        httpClientMock.verify().get().called();
        httpClientMock.verify().post().called();
        httpClientMock.verify().delete().called();
        httpClientMock.verify().put().called();
        httpClientMock.verify().options().called();
        httpClientMock.verify().head().called();
        httpClientMock.verify().patch().called();
    }

    @Test
    void shouldCountNumberOfHttpMethodCalls() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onGet("http://localhost").doReturn("empty");
        httpClientMock.onPost("http://localhost").doReturn("empty");
        httpClientMock.onPost("http://localhost").doReturn("empty");
        httpClientMock.onDelete("http://localhost").doReturn("empty");
        httpClientMock.onDelete("http://localhost").doReturn("empty");
        httpClientMock.onDelete("http://localhost").doReturn("empty");

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
    void shouldCountNumberOfUrlCalls() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost").doReturn("empty");
        httpClientMock.onGet("http://www.google.com").doReturn("empty");
        httpClientMock.onGet("http://www.google.com").doReturn("empty");
        httpClientMock.onGet("http://example.com").doReturn("empty");
        httpClientMock.onGet("http://example.com").doReturn("empty");
        httpClientMock.onGet("http://example.com").doReturn("empty");

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

    @Test
    void shouldVerifyBodyContent() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onPost("http://localhost").withBody(containsString("foo")).doReturn("empty");
        httpClientMock.onPost("http://localhost").withBody(containsString("foo")).doReturn("empty");
        httpClientMock.onPut("http://localhost").withBody(containsString("bar")).doReturn("empty");
        httpClientMock.onPut("http://localhost").withBody(containsString("foo")).doReturn("empty");

        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());

        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(HttpRequest.BodyPublishers.ofString("bar")).build(), ofString());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());

        httpClientMock.verify().post("http://localhost").withBody(containsString("foo")).called(2);
        httpClientMock.verify().put("http://localhost").withBody(containsString("bar")).called();
        httpClientMock.verify().get("http://localhost").withBody(containsString("foo bar")).notCalled();
    }

    @Test
    void should_handle_path_with_query_parameter() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onPost("http://localhost").withParameter("a", "1").withParameter("b", "2").withParameter("c", "3").doReturn("empty");
        httpClientMock.onPost("http://localhost").withParameter("a", "1").withParameter("b", "2").doReturn("empty");
        httpClientMock.onPost("http://localhost").withParameter("a", "1").doReturn("empty");

        httpClientMock.send(newBuilder(URI.create("http://localhost?a=1&b=2&c=3")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost?a=1&b=2")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost?a=1")).POST(noBody()).build(), discarding());

        httpClientMock.verify().post("http://localhost?d=3").notCalled();
        httpClientMock.verify().post("http://localhost?a=3").notCalled();
        httpClientMock.verify().post("http://localhost?a=1&b=2&c=3").called(1);
        httpClientMock.verify().post("http://localhost?a=1&b=2").called(1);
        httpClientMock.verify().post("http://localhost?a=1").called(1);
        httpClientMock.verify().post("http://localhost").withParameter("a", "1").called(1);

        httpClientMock.verify().post("http://localhost").notCalled();
    }

    @Test
    void should_handle_path_with_reference() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onPost().withParameter("a", "1").withReference("abc").doReturn("empty");
        httpClientMock.onPost().withReference("xyz").doReturn("empty");

        httpClientMock.send(newBuilder(URI.create("http://localhost?a=1#abc")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost#xyz")).POST(noBody()).build(), discarding());

        httpClientMock.verify().post("http://localhost?a=1#abc").called(1);
        httpClientMock.verify().post("http://localhost#abc").notCalled();
        httpClientMock.verify().post("http://localhost#xyz").called(1);
        httpClientMock.verify().post("http://localhost").notCalled();
    }

    @Test
    void should_throw_exception_when_number_of_calls_is_wrong() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            final HttpClientMock httpClientMock = new HttpClientMock();

            httpClientMock.send(newBuilder(URI.create("http://localhost?a=1")).POST(noBody()).build(), discarding());

            httpClientMock.verify()
                    .post("http://localhost?a=1#abc")
                    .called(2);
        });
    }

    @Test
    void should_allow_different_host_then_default() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("http://www.google.com").doReturn("google");

        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://www.google.com")).GET().build(), discarding());

        httpClientMock.verify().get("/login").called();
        httpClientMock.verify().get("http://www.google.com").called();
    }

    @Test
    void should_check_header() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onGet("/login").doReturn("OK");

        httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().header("User-Agent", "Chrome").build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().header("User-Agent", "Mozilla").build(), discarding());

        httpClientMock.verify().get("/login").withHeader("User-Agent", "Mozilla").called();
        httpClientMock.verify().get("/login").withHeader("User-Agent", "Chrome").called();
        httpClientMock.verify().get("/login").withHeader("User-Agent", "IE").notCalled();
    }

    @Test
    void should_verify_each_part_of_URL_in_separate() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost:8080/login?foo=bar#ref").doReturn("OK");
        httpClientMock.send(TestRequests.get("http://localhost:8080/login?foo=bar#ref"), discarding());

        httpClientMock.verify().get().withHost("localhost").called();
        httpClientMock.verify().get().withHost("google").notCalled();
        httpClientMock.verify().get().withPath("/login").called();
        httpClientMock.verify().get().withPath("/logout").notCalled();
        httpClientMock.verify().get().withParameter("foo","bar").called();
        httpClientMock.verify().get().withParameter("foo","hoo").notCalled();
        httpClientMock.verify().get().withReference("ref").called();
        httpClientMock.verify().get().withReference("fer").notCalled();

    }

    @Test
    void should_verify_custom_condition() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost:8080/login?foo=bar#ref").doReturn("OK");
        httpClientMock.send(TestRequests.get("http://localhost:8080/login?foo=bar#ref"), discarding());

        httpClientMock.verify().get().with(request -> request.uri().getFragment().length()==3).called();
    }

}
