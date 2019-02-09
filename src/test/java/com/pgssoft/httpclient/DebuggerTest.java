package com.pgssoft.httpclient;

import com.pgssoft.httpclient.debug.Debugger;
import com.pgssoft.httpclient.rule.Rule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static com.pgssoft.httpclient.TestRequests.*;
import static com.pgssoft.httpclient.TestRequests.post;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

public class DebuggerTest {

    private HttpClientMock httpClientMock;
    private TestDebugger debugger;

    @BeforeEach
    public void setUp() {
        debugger = new TestDebugger();
        httpClientMock = new HttpClientMock("http://localhost", debugger);
    }

    @Test
    public void should_print_all_request_with_no_matching_rules() throws Exception {
        httpClientMock.onGet("/admin").doReturn("admin");

        try {
            httpClientMock.send(get("http://localhost/login"), discarding());
            httpClientMock.send(newBuilder(URI.create("http://localhost/admin")).GET().build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }

        assertThat(debugger.requests, hasItem("http://localhost/login"));
        assertThat(debugger.requests, not(hasItem("http://localhost/admin")));
    }

    @Test
    public void should_print_all_request_when_debugging_is_turn_on() throws Exception {
        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("/user").doReturn("user");
        httpClientMock.onGet("/admin").doReturn("admin");

        httpClientMock.debugOn();
        httpClientMock.send(get("http://localhost/login"), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost/user")).GET().build(), discarding());
        httpClientMock.debugOff();
        httpClientMock.send(newBuilder(URI.create("http://localhost/admin")).GET().build(), discarding());

        assertThat(debugger.requests, hasItem("http://localhost/login"));
        assertThat(debugger.requests, hasItem("http://localhost/user"));
        assertThat(debugger.requests, not(hasItem("http://localhost/admin")));
    }

    @Test
    public void should_debug_header_condition() throws Exception {
        httpClientMock
                .onGet("/login").withHeader("User-Agent", "Mozilla")
                .doReturn("mozilla");

        try {
            httpClientMock.debugOn();
            httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().header("User-Agent", "Mozilla").build(), ofString());
            httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().header("User-Agent", "Chrome").build(), ofString());
            httpClientMock.debugOff();
        } catch (IllegalStateException e) {
            // discard the exception
        }

        assertTrue(debugger.matching.contains("header User-Agent is \"Mozilla\""));
        assertFalse(debugger.notMatching.contains("header User-Agent is \"Chrome\""));
    }

    @Test
    public void should_put_message_about_missing_parameter() throws Exception {
        httpClientMock.onGet("/login").withParameter("foo", "bar");
        try {
            httpClientMock.send(get("http://localhost/login"), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("all URI parameters have matching value"));
    }

    @Test
    public void should_put_message_about_matching_parameter() throws Exception {
        httpClientMock
                .onGet("/login").withParameter("foo", "bar")
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bar")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("all URI parameters have matching value"));
    }

    @Test
    public void should_put_message_about_not_matching_parameter() throws Exception {
        httpClientMock.onGet("/login")
                .withParameter("foo", "bar")
                .doReturn("login");
        try {
            httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bbb")).GET().build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("all URI parameters have matching value"));
    }

    @Test
    public void should_put_message_about_redundant_parameter() throws Exception {
        httpClientMock.onGet("/login")
                .doReturn("login");
        try {
            httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bbb")).GET().build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("all URI parameters have matching value"));
    }

    @Test
    public void should_put_message_with_all_parameter_matchers() throws Exception {
        httpClientMock.onGet("/login")
                .withParameter("foo", Matchers.allOf(Matchers.startsWith("a"), Matchers.endsWith("b")))
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=aabb")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("all URI parameters have matching value"));
    }

    @Test
    public void should_put_message_about_not_matching_reference() throws Exception {
        httpClientMock.onGet("/login#foo")
                .doReturn("login");
        try {
            httpClientMock.send(get("http://localhost/login"), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("URI reference has matching value"));
    }

    @Test
    public void should_put_message_about_matching_reference() throws Exception {
        httpClientMock.onGet("/login#foo")
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login#foo")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("URI reference has matching value"));
    }


    @Test
    public void should_put_message_about_matching_http_method() throws Exception {
        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(get("http://localhost/login"), discarding());
        assertTrue(debugger.matching.contains("HTTP method is GET"));
    }

    @Test
    public void should_put_message_about_not_matching_http_method() throws Exception {
        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.debugOn();
        try {
            httpClientMock.send(post("http://localhost/login"), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("HTTP method is GET"));
    }

    @Test
    public void should_put_message_about_not_matching_URL() throws Exception {
        httpClientMock.onGet("http://localhost:8080/login").doReturn("login");
        httpClientMock.debugOn();
        try {
            httpClientMock.send(newBuilder(URI.create("https://www.google.com")).POST(noBody()).build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.notMatching.contains("schema is \"http\""));
        assertTrue(debugger.notMatching.contains("host is \"localhost\""));
        assertTrue(debugger.notMatching.contains("path is \"/login\""));
        assertTrue(debugger.notMatching.contains("port is <8080>"));
    }

    @Test
    public void should_put_message_about_matching_URL() throws Exception {
        httpClientMock.onGet("http://localhost:8080/login").doReturn("login");
        httpClientMock.debugOn();

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost:8080/login"), discarding()));
        assertTrue(debugger.matching.contains("schema is \"http\""));
        assertTrue(debugger.matching.contains("host is \"localhost\""));
        assertTrue(debugger.matching.contains("path is \"/login\""));
        assertTrue(debugger.matching.contains("port is <8080>"));
    }

    @Test
    public void should_use_anonymous_message_for_conditions_without_debug_message() throws Exception {
        httpClientMock.onGet("http://localhost:8080/login")
                .with(new TestCondition())
                .doReturn("login");
        httpClientMock.debugOn();
        try {
            httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.matching.contains("Anonymous condition"));
    }

    @Test
    public void should_use_anonymous_message_for_lambda_conditions() throws Exception {
        httpClientMock.onGet("http://localhost:8080/login")
                .with(req -> true)
                .doReturn("login");
        httpClientMock.debugOn();
        try {
            httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), discarding());
        } catch (IllegalStateException e) {
            // discard exception
        }
        assertTrue(debugger.matching.contains("Anonymous condition"));
    }


    private class TestDebugger extends Debugger {
        private final ArrayList<String> matching = new ArrayList<>();
        private final ArrayList<String> notMatching = new ArrayList<>();
        private final ArrayList<String> requests = new ArrayList<>();

        @Override
        public void debug(List<Rule> rules, HttpRequest request) {
            this.requests.add(request.uri().toString());
            super.debug(rules, request);
        }

        @Override
        public void message(boolean matching, String expected) {
            super.message(matching, expected);
            if (matching) {
                this.matching.add(expected);
            } else {
                this.notMatching.add(expected);
            }
        }
    }
}


class TestCondition implements Condition {

    @Override
    public boolean matches(HttpRequest request) {
        return true;
    }
}