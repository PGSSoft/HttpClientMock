package com.pgssoft;

import com.pgssoft.debug.Debugger;
import com.pgssoft.rule.Rule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().build(), discarding());
        httpClientMock.send(newBuilder(URI.create("http://localhost/admin")).GET().build(), discarding());

        assertThat(debugger.requests, hasItem("http://localhost/login"));
        assertThat(debugger.requests, not(hasItem("http://localhost/admin")));
    }

    @Test
    public void should_print_all_request_when_debugging_is_turn_on() throws Exception {
        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("/user").doReturn("user");
        httpClientMock.onGet("/admin").doReturn("admin");

        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().build(), discarding());
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

        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().header("User-Agent", "Mozilla").build(), ofString());
        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().header("User-Agent", "Chrome").build(), ofString());
        httpClientMock.debugOff();

        assertTrue(debugger.matching.contains("header User-Agent is \"Mozilla\""));
        assertFalse(debugger.notMatching.contains("header User-Agent is \"Chrome\""));
    }

    @Test
    public void should_put_message_about_missing_parameter() throws Exception {
        httpClientMock.onGet("/login").withParameter("foo", "bar");
        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().build(), discarding());
        assertTrue(debugger.notMatching.contains("parameter foo occurs in request"));
    }

    @Test
    public void should_put_message_about_matching_parameter() throws Exception {
        httpClientMock
                .onGet("/login").withParameter("foo", "bar")
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bar")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("parameter foo is \"bar\""));
    }

    @Test
    public void should_put_message_about_not_matching_parameter() throws Exception {
        httpClientMock.onGet("/login")
                .withParameter("foo", "bar")
                .doReturn("login");
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bbb")).GET().build(), discarding());
        assertTrue(debugger.notMatching.contains("parameter foo is \"bar\""));
    }

    @Test
    public void should_put_message_about_redundant_parameter() throws Exception {
        httpClientMock.onGet("/login")
                .doReturn("login");
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=bbb")).GET().build(), discarding());
        assertTrue(debugger.notMatching.contains("parameter foo is redundant"));
    }

    @Test
    public void should_put_message_with_all_parameter_matchers() throws Exception {
        httpClientMock.onGet("/login")
                .withParameter("foo", Matchers.startsWith("a"))
                .withParameter("foo", Matchers.endsWith("b"))
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login?foo=aabb")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("parameter foo is a string starting with \"a\" and a string ending with \"b\""));
    }

    @Test
    public void should_put_message_about_not_matching_reference() throws Exception {
        httpClientMock.onGet("/login#foo")
                .doReturn("login");
        httpClientMock.send(newBuilder(URI.create("http://localhost/login")).GET().build(), discarding());
        assertTrue(debugger.notMatching.contains("reference is \"foo\""));
    }

    @Test
    public void should_put_message_about_matching_reference() throws Exception {
        httpClientMock.onGet("/login#foo")
                .doReturn("login");
        httpClientMock.debugOn();
        httpClientMock.send(newBuilder(URI.create("http://localhost/login#foo")).GET().build(), discarding());
        assertTrue(debugger.matching.contains("reference is \"foo\""));
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
