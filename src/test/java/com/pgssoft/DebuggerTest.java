package com.pgssoft;

import com.pgssoft.debug.Debugger;
import com.pgssoft.rule.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

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

    private class TestDebugger extends Debugger {
        private final ArrayList<String> matching = new ArrayList<>();
        private final ArrayList<String> notMatching = new ArrayList<>();
        private final ArrayList<String> requests = new ArrayList<>();

        @Override
        public void debug(List<Rule> rules, HttpRequest request) {
            this.requests.add(request.uri().toString());
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
