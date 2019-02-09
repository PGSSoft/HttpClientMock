package com.pgssoft.httpclient;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.pgssoft.httpclient.matchers.HttpResponseMatchers.hasContent;
import static com.pgssoft.httpclient.matchers.HttpResponseMatchers.hasStatus;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

class HttpClientResponseBuilderTest {

    @Test
    void should_throw_exception_when_no_rule_matches() {
        assertThrows(IllegalStateException.class, () -> {
            HttpClientMock httpClientMock = new HttpClientMock();
            httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        });
    }

    @Test
    void should_use_next_action_after_every_call() throws Exception {
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
        final var bar2 = httpClientMock.send(newBuilder(URI.create("http://localhost/bar")).GET().build(), discarding());
        assertThat(bar1, hasContent("bar"));
        assertThat(bar2, hasStatus(300));

        assertThrows(IOException.class, () -> httpClientMock.send(newBuilder(URI.create("http://localhost/bar")).GET().build(), ofString()));

    }

    @Test
    void should_support_response_in_different_charsets() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo")
                .doReturn("first")
                .doReturn("second", Charset.forName("UTF-16"))
                .doReturn("third", Charset.forName("ASCII"));

        final var response1 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        final var response2 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString(StandardCharsets.UTF_16));
        final var response3 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString(StandardCharsets.US_ASCII));

        assertThat(response1, hasContent("first"));
        assertThat(response2, hasContent("second"));
        assertThat(response3, hasContent("third"));
    }

    @Test
    void shouldFailToDecodeBodyWhenDifferentCharsetsUsed() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
        httpClientMock.onGet("/foo").doReturn("output", StandardCharsets.UTF_16);

        final var response1 = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString(StandardCharsets.UTF_8));

        assertThat(response1, not(hasContent("output")));
    }

    @Test
    void should_support_response_in_body_with_status() throws Exception {
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
    void should_throw_exception_when_throwing_action_matched() {
        assertThrows(IOException.class, () -> {
            HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
            httpClientMock.onGet("/foo").doThrowException(new IOException());
            httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), discarding());
        });
    }

    @Test
    void should_return_status_corresponding_to_match() throws Exception {
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
    void should_do_custom_action() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onPost("/login").doAction(customAction());

        final var response = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), ofString());

        assertThat(response, hasContent("I am a custom action"));

    }

    @Test
    void should_add_header_to_response() throws Exception {
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
    void should_add_status_to_response() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturn("foo").withStatus(300);

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());

        assertThat(login, hasContent("foo"));
        assertThat(login, hasStatus(300));

    }

    @Test
    void should_return_json_with_right_header() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/foo")
                .doReturnJSON("{foo:1}", Charset.forName("UTF-16"));
        httpClientMock.onGet("/bar")
                .doReturnJSON("{bar:1}");

        final var foo = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), ofString());
        final var bar = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/bar")).GET().build(), ofString());
        assertThat(foo, hasContent("{foo:1}"));
        assertTrue(foo.headers().firstValue("Content-type").isPresent());
        assertThat(foo.headers().firstValue("Content-type").get(), equalTo("application/json; charset=UTF-16"));

        assertThat(bar, hasContent("{bar:1}"));
        assertTrue(bar.headers().firstValue("Content-type").isPresent());
        assertThat(bar.headers().firstValue("Content-type").get(), equalTo("application/json; charset=UTF-8"));
    }

    @Test
    void should_return_xml_with_right_header() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/foo")
                .doReturnXML("<foo>bar</foo>", Charset.forName("UTF-16"));
        httpClientMock.onGet("/bar")
                .doReturnXML("<foo>bar</foo>");
        final var foo = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), ofString());
        final var bar = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/bar")).GET().build(), ofString());

        assertThat(foo, hasContent("<foo>bar</foo>"));
        assertThat(foo.headers().firstValue("Content-type").orElse(null), equalTo("application/xml; charset=UTF-16"));

        assertThat(bar, hasContent("<foo>bar</foo>"));
        assertThat(bar.headers().firstValue("Content-type").orElse(null), equalTo("application/xml; charset=UTF-8"));
    }

    @Test
    void should_allow_to_return_response_without_body() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturnStatus(204);   // no content
        var request = newBuilder(URI.create("http://localhost:8080/login")).GET().build();
        var response = httpClientMock.send(request, BodyHandlers.ofString());

        assertEquals("", response.body());
    }

    @Test
    void should_throw_exception_when_body_matcher_is_present_on_post_request() {
        assertThrows(IllegalStateException.class, () -> {
            HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
            httpClientMock.onPost("/path1")
                    .withBody(equalTo("Body content"))
                    .doReturnStatus(200);

            httpClientMock.send(newBuilder(URI.create("http://localhost:8080/path2")).GET().build(), discarding());
        });
    }

    @Test
    void shouldRespectBodyHandler() throws Exception {
        final HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
        // A simple string is passed to indicate what we want to see in the response
        httpClientMock.onGet().doReturn("expected\nnewline");

        // A BodyHandler is passed that transforms into a Stream of String based on newline characters
        final var response = httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), BodyHandlers.ofLines());

        // We expect out String to be returned in the form that the BodyHandler requires - a Stream of Strings
        final List<String> responseList = response.body().collect(Collectors.toList());
        assertThat(responseList.size(), equalTo(2));
        assertThat(responseList.get(0), equalTo("expected"));
        assertThat(responseList.get(1), equalTo("newline"));
    }

    @Test
    void shouldTransformStringToInputStream() throws Exception {
        final String expectedString = "expected";
        final HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
        httpClientMock.onGet().doReturn(expectedString);

        final var response = httpClientMock.send(newBuilder(URI.create("http://localhost")).GET().build(), BodyHandlers.ofInputStream());

        final InputStream output = response.body();
        final String outputString = new BufferedReader(new InputStreamReader(output)).readLine();
        assertThat(outputString, equalTo(expectedString));
    }

    private Action customAction() {
        return r -> r.setBodyBytes(ByteBuffer.wrap("I am a custom action".getBytes()));
    }
}
