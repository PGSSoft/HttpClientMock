package com.pgssoft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.Matchers.containsString;
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

    @Test
    public void shouldVerifyBodyContent() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());

        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(HttpRequest.BodyPublishers.ofString("bar")).build(), ofString());
        httpClientMock.send(newBuilder(URI.create("http://localhost")).PUT(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());

        httpClientMock.verify().post("http://localhost").withBody(containsString("foo")).called(2);
        httpClientMock.verify().put("http://localhost").withBody(containsString("bar")).called();
        httpClientMock.verify().get("http://localhost").withBody(containsString("foo bar")).notCalled();
    }

    @Test
    public void should_handle_path_with_query_parameter() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

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
    public void should_handle_path_with_reference() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.send(newBuilder(URI.create("http://localhost?a=1#abc")).POST(noBody()).build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost#xyz")).POST(noBody()).build(), discarding());

        httpClientMock.verify().post("http://localhost?a=1#abc").called(1);
        httpClientMock.verify().post("http://localhost#abc").notCalled();
        httpClientMock.verify().post("http://localhost#xyz").called(1);
        httpClientMock.verify().post("http://localhost").notCalled();
    }

    @Test
    public void should_throw_exception_when_number_of_calls_is_wrong() throws IOException {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            HttpClientMock httpClientMock = new HttpClientMock();

            httpClientMock.send(newBuilder(URI.create("http://localhost?a=1")).POST(noBody()).build(), discarding());

            httpClientMock.verify()
                    .post("http://localhost?a=1#abc")
                    .called(2);
        });
    }
}
