package com.pgssoft;

import com.pgssoft.action.Action;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

import static com.pgssoft.Asserts.assertThrows;
import static com.pgssoft.matchers.HttpResponseMatchers.hasContent;
import static com.pgssoft.matchers.HttpResponseMatchers.hasStatus;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpClientResponseBuilderTest {

    @Test
    public void should_return_status_404_when_no_rule_matches() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();
        final var notFound = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        assertNull(notFound);
        // TODO: Adjust for exception
    }

    @Test
    public void should_use_next_action_after_every_call() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo")
                .doReturn("first")
                .doReturn("second")
                .doReturn("third");

        httpClientMock.onGet("/bar")
                .doReturn("bar")
                .doReturnStatus(300)
                .doThrowException(new IOException());

        final var response1 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response2 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response3 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response4 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response5 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());

        assertThat(response1, hasContent("first"));
        assertThat(response2, hasContent("second"));
        assertThat(response3, hasContent("third"));
        assertThat(response4, hasContent("third"));
        assertThat(response5, hasContent("third"));

        final var bar1 = httpClientMock.send(newBuilder(URI.create("http://localhost/bar")).GET().build(), ofString());
        final var bar2 = httpClientMock.send(newBuilder(URI.create("http://localhost/bar")).GET().build(), ofString());
        assertThat(bar1, hasContent("bar"));
        assertThat(bar2, hasStatus(300));

        assertThrows(IOException.class, () -> httpClientMock.send(newBuilder(URI.create("http://localhost/bar")).GET().build(), ofString()));

    }

    @Test
    public void should_support_response_in_body_with_status() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo")
                .doReturn("first")
                .doReturn(300, "second")
                .doReturn(400, "third");

        final var response1 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response2 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response3 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response4 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response5 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());

        assertThat(response1, hasContent("first"));
        assertThat(response1, hasStatus(200));
        assertThat(response2, hasContent("second"));
        assertThat(response2, hasStatus(300));
        assertThat(response3, hasContent("third"));
        assertThat(response3, hasStatus(400));
        assertThat(response4, hasContent("third"));
        assertThat(response4, hasStatus(400));
        assertThat(response5, hasContent("third"));
        assertThat(response5, hasStatus(400));
    }

    @Test
    public void should_throw_exception_when_throwing_action_matched() throws IOException {
        Assertions.assertThrows(IOException.class, () -> {
            HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
            httpClientMock.onGet("/foo").doThrowException(new IOException());
            httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), discarding());
        });
    }

    @Test
    public void should_return_status_corresponding_to_match() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onGet("/login").doReturnStatus(200);
        httpClientMock.onGet("/abc").doReturnStatus(404);
        httpClientMock.onGet("/error").doReturnStatus(500);

        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), discarding());
        final var notFound = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/abc")).GET().build(), discarding());
        final var error = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/error")).GET().build(), discarding());

        assertThat(ok, hasStatus(200));
        assertThat(notFound, hasStatus(404));
        assertThat(error, hasStatus(500));
    }

    @Test
    public void should_do_custom_action() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onPost("/login").doAction(customAction());

        final var response = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), ofString());

        assertThat(response, hasContent("I am a custom action"));

    }

    @Test
    public void should_add_header_to_response() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onPost("/login")
                .doReturn("foo").withHeader("tracking", "123")
                .doReturn("foo").withHeader("tracking", "456");

        final var first = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), ofString());
        final var second = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), ofString());

        assertThat(first.headers().firstValue("tracking").orElse(null), equalTo("123"));
        assertThat(second.headers().firstValue("tracking").orElse(null), equalTo("456"));
    }

    @Test
    public void should_add_status_to_response() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturn("foo").withStatus(300);
        //HttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());

        assertThat(login, hasContent("foo"));
        assertThat(login, hasStatus(300));

    }

    @Test
    public void should_return_json_with_right_header() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturnJSON("{foo:1}", Charset.forName("UTF-8"));
        //HttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());

        assertThat(login, hasContent("{foo:1}"));
        assertThat(login.headers().firstValue("Content-type").orElse(null), equalTo("application/json"));
    }

    @Test
    public void should_return_xml_with_right_header() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturnXML("<foo>bar</foo>", Charset.forName("UTF-8"));
        //HttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());

        assertThat(login, hasContent("<foo>bar</foo>"));
        assertThat(login.headers().firstValue("Content-type").orElse(null), equalTo("application/xml"));
    }

    private Action customAction() {
        return r -> {
            r.setBody("I am a custom action");
        };
    }
}
