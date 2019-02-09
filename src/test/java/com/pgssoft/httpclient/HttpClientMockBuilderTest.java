package com.pgssoft.httpclient;

import com.pgssoft.httpclient.matchers.HttpResponseMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.pgssoft.httpclient.TestRequests.get;
import static com.pgssoft.httpclient.TestRequests.post;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpClientMockBuilderTest {

    @Test
    void shouldMatchSeparateHostAndPath() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("localhost")
                .withPath("/login")
                .doReturnStatus(200);

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://www.google.com/login"), discarding()));
        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/foo"), discarding()));
        assertThat(httpClientMock.send(post("http://localhost/login"), discarding()).statusCode(), equalTo(200));
    }

    @Test
    void shouldMatchSeparatePathAndParameter() throws Exception {
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

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login?a=1&b=2"), ofString()));

        MatcherAssert.assertThat(firstResponse, HttpResponseMatchers.hasContent("one"));
        MatcherAssert.assertThat(secondResponse, HttpResponseMatchers.hasContent("two"));
    }

    @Test
    void onXXXX_methods_with_path_should_add_method_and_path_condition() throws Exception {
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

        MatcherAssert.assertThat(getResponse, HttpResponseMatchers.hasContent("get"));
        MatcherAssert.assertThat(postResponse, HttpResponseMatchers.hasContent("post"));
        MatcherAssert.assertThat(putResponse, HttpResponseMatchers.hasContent("put"));
        MatcherAssert.assertThat(deleteResponse, HttpResponseMatchers.hasContent("delete"));
        MatcherAssert.assertThat(headResponse, HttpResponseMatchers.hasContent("head"));
        MatcherAssert.assertThat(optionsResponse, HttpResponseMatchers.hasContent("options"));
        MatcherAssert.assertThat(patchResponse, HttpResponseMatchers.hasContent("patch"));
    }


    @Test
    void onXXXX_methods_should_add_method_condition() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet().doReturn("get");
        httpClientMock.onPost().doReturn("post");
        httpClientMock.onPut().doReturn("put");
        httpClientMock.onDelete().doReturn("delete");
        httpClientMock.onHead().doReturn("head");
        httpClientMock.onOptions().doReturn("options");
        httpClientMock.onPatch().doReturn("patch");

        var getResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        var postResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).POST(noBody()).build(), ofString());
        var putResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).PUT(noBody()).build(), ofString());
        var deleteResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).DELETE().build(), ofString());
        var headResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("HEAD", noBody()).build(), ofString());
        var optionsResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("OPTIONS", noBody()).build(), ofString());
        var patchResponse = httpClientMock.send(newBuilder(URI.create("http://localhost/foo")).method("PATCH", noBody()).build(), ofString());

        MatcherAssert.assertThat(getResponse, HttpResponseMatchers.hasContent("get"));
        MatcherAssert.assertThat(postResponse, HttpResponseMatchers.hasContent("post"));
        MatcherAssert.assertThat(putResponse, HttpResponseMatchers.hasContent("put"));
        MatcherAssert.assertThat(deleteResponse, HttpResponseMatchers.hasContent("delete"));
        MatcherAssert.assertThat(headResponse, HttpResponseMatchers.hasContent("head"));
        MatcherAssert.assertThat(optionsResponse, HttpResponseMatchers.hasContent("options"));
        MatcherAssert.assertThat(patchResponse, HttpResponseMatchers.hasContent("patch"));
    }

    @Test
    void shouldCheckCustomRule() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        final Condition fooCondition = request -> request.uri().toString().contains("foo");
        httpClientMock.onGet("http://localhost/foo/bar")
                .with(fooCondition)
                .doReturn("yes");

        final HttpResponse first = httpClientMock.send(newBuilder(URI.create("http://localhost/foo/bar")).GET().build(), ofString());

        MatcherAssert.assertThat(first, HttpResponseMatchers.hasContent("yes"));
    }

    @Test
    void shouldUseRightHostAndPath() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onGet("http://localhost:8080/foo").doReturn("localhost");
        httpClientMock.onGet("http://www.google.com").doReturn("google");
        httpClientMock.onGet("https://www.google.com").doReturn("https");

        final HttpResponse localhost = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo")).GET().build(), ofString());
        final HttpResponse google = httpClientMock.send(get("http://www.google.com"), ofString());
        final HttpResponse https = httpClientMock.send(newBuilder(URI.create("https://www.google.com")).GET().build(), ofString());

        MatcherAssert.assertThat(localhost, HttpResponseMatchers.hasContent("localhost"));
        MatcherAssert.assertThat(google, HttpResponseMatchers.hasContent("google"));
        MatcherAssert.assertThat(https, HttpResponseMatchers.hasContent("https"));
    }

    @Test
    void shouldMatchRightHeaderValue() throws Exception {
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

        MatcherAssert.assertThat(getMozilla, HttpResponseMatchers.hasContent("mozilla"));
        MatcherAssert.assertThat(getChrome, HttpResponseMatchers.hasContent("chrome"));
        //assertThat(httpClientMock.execute(getSafari), hasStatus(404));
    }

    @Test
    void should_match_right_parameter_value() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/foo").withParameter("id", "1").withParameter("name", "abc")
                .doReturn("one");
        httpClientMock
                .onGet("/foo").withParameter("id", "2")
                .doReturn("two");

        final var one = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo?id=1&name=abc")).GET().build(), ofString());
        final var two = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo?id=2")).GET().build(), ofString());

        MatcherAssert.assertThat(one, HttpResponseMatchers.hasContent("one"));
        MatcherAssert.assertThat(two, HttpResponseMatchers.hasContent("two"));
    }

    @Test
    void should_add_default_host_to_every_relative_path() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("/product/search").doReturn("search");
        httpClientMock.onGet("/logout").doReturn("logout");

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());
        final var search = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/product/search")).GET().build(), ofString());
        final var logout = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/logout")).GET().build(), ofString());

        MatcherAssert.assertThat(login, HttpResponseMatchers.hasContent("login"));
        MatcherAssert.assertThat(search, HttpResponseMatchers.hasContent("search"));
        MatcherAssert.assertThat(logout, HttpResponseMatchers.hasContent("logout"));
    }

    @Test
    void checkBody() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onPost("/login")
                .doReturnStatus(500);
        httpClientMock.onPost("/login").withBody(containsString("foo"))
                .doReturnStatus(200);

        final var badLogin = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), discarding());
        final var correctLogin = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), discarding());

        MatcherAssert.assertThat(correctLogin, HttpResponseMatchers.hasStatus(200));
        MatcherAssert.assertThat(badLogin, HttpResponseMatchers.hasStatus(500));
    }

    @Test
    void when_url_contains_parameter_it_should_be_added_us_a_separate_condition() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?user=john")
                .doReturnStatus(400);
        httpClientMock.onPost("/login?user=john&pass=abc")
                .doReturnStatus(200);

//        HttpResponse notFound = httpClientMock.execute(new HttpPost("http://localhost/login"));
//        HttpResponse notFound_2 = httpClientMock.execute(new HttpPost("http://localhost/login?user=john&pass=abc&foo=bar"));

        final var wrong = httpClientMock.send(post("http://localhost/login?user=john"), discarding());
        final var ok = httpClientMock.send(post("http://localhost/login?user=john&pass=abc"), discarding());

        //assertThat(notFound, hasStatus(404));
        MatcherAssert.assertThat(wrong, HttpResponseMatchers.hasStatus(400));
        MatcherAssert.assertThat(ok, HttpResponseMatchers.hasStatus(200));
        //assertThat(notFound_2, hasStatus(404));
    }

    @Test
    void should_not_match_URL_with_missing_param() {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?user=john")
                .doReturnStatus(400);
        httpClientMock.onPost("/login?user=john&pass=abc")
                .doReturnStatus(200);
        var request = newBuilder(URI.create("http://localhost/login")).POST(noBody()).build();
        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(request, discarding()));
    }

    @Test
    void should_not_match_URL_with_surplus_param() {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?user=john")
                .doReturnStatus(200);
        var request = newBuilder(URI.create("http://localhost/login?user=john&pass=abc")).POST(noBody()).build();
        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(request, discarding()));
    }


    @Test
    void should_handle_path_with_parameters_and_reference() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?p=1#abc")
                .doReturnStatus(200);

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login"), discarding()));
        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login?p=1"), discarding()));
        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login#abc"), discarding()));

        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost/login?p=1#abc")).POST(noBody()).build(), discarding());
        MatcherAssert.assertThat(ok, HttpResponseMatchers.hasStatus(200));
    }

    @Test
    void should_check_reference_value() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .doReturnStatus(400);
        httpClientMock.onPost("/login")
                .withReference("ref")
                .doReturnStatus(200);

        final var wrong = httpClientMock.send(newBuilder(URI.create("http://localhost/login")).POST(noBody()).build(), discarding());
        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost/login#ref")).POST(noBody()).build(), discarding());

        MatcherAssert.assertThat(wrong, HttpResponseMatchers.hasStatus(400));
        MatcherAssert.assertThat(ok, HttpResponseMatchers.hasStatus(200));
    }

    @Test
    void after_reset_every_call_should_throw_exception() {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.reset();

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login"), discarding()));
    }

    @Test
    void after_reset_number_of_calls_should_be_zero() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.send(post("http://localhost/login"), discarding());
        httpClientMock.send(post("http://localhost/login"), discarding());
        httpClientMock.reset();
        httpClientMock.verify().post("/login").notCalled();

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.send(post("http://localhost/login"), discarding());
        httpClientMock.send(post("http://localhost/login"), discarding());
        httpClientMock.verify().post("/login").called(2);

    }

    @Test
    void not_all_parameters_occurred() {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .withParameter("foo", "bar")
                .doReturnStatus(200);

        assertThrows(NoMatchingRuleException.class, () -> httpClientMock.send(post("http://localhost/login"), ofString()));
    }

    @Test
    void should_allow_different_host_then_default() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("http://www.google.com").doReturn("google");

        final var login = httpClientMock.send(get("http://localhost/login"), ofString());
        final var google = httpClientMock.send(get("http://www.google.com"), ofString());

        MatcherAssert.assertThat(login, HttpResponseMatchers.hasContent("login"));
        MatcherAssert.assertThat(google, HttpResponseMatchers.hasContent("google"));
    }

    @Test
    void response_should_contain_request_headers_body_uri_version() throws IOException, URISyntaxException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
        httpClientMock.onGet("/login")
                .doReturn("login")
                .withHeader("foo", "bar");
        var request = get("http://localhost/login");
        var response = httpClientMock.send(request, ofString());
        assertThat(response.uri(), Matchers.equalTo(new URI("http://localhost/login")));
        assertThat(response.request(), Matchers.equalTo(request));
        assertThat(response.body(), Matchers.equalTo("login"));
        assertTrue(response.headers().firstValue("foo").isPresent());
        assertThat(response.headers().firstValue("foo").get(), Matchers.equalTo("bar"));
        assertThat(response.version(), Matchers.equalTo(HttpClient.Version.HTTP_1_1));

    }


}
