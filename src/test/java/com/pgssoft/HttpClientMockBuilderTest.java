package com.pgssoft;

import com.pgssoft.condition.Condition;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.pgssoft.matchers.HttpResponseMatchers.hasContent;
import static com.pgssoft.matchers.HttpResponseMatchers.hasStatus;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
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

    @Test
    public void should_match_right_parameter_value() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/foo").withParameter("id", "1").withParameter("name", "abc")
                .doReturn("one");
        httpClientMock
                .onGet("/foo").withParameter("id", "2")
                .doReturn("two");

        final var one = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo?id=1&name=abc")).GET().build(), ofString());
        final var two = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/foo?id=2")).GET().build(), ofString());

        assertThat(one, hasContent("one"));
        assertThat(two, hasContent("two"));
    }

    @Test
    public void should_add_default_host_to_every_relative_path() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("/product/search").doReturn("search");
        httpClientMock.onGet("/logout").doReturn("logout");

        final var login = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).GET().build(), ofString());
        final var search = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/product/search")).GET().build(), ofString());
        final var logout = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/logout")).GET().build(), ofString());

        assertThat(login, hasContent("login"));
        assertThat(search, hasContent("search"));
        assertThat(logout, hasContent("logout"));
    }

    @Test
    public void checkBody() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onPost("/login")
                .doReturnStatus(500);
        httpClientMock.onPost("/login").withBody(containsString("foo"))
                .doReturnStatus(200);

        final var badLogin = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(noBody()).build(), ofString());
        final var correctLogin = httpClientMock.send(newBuilder(URI.create("http://localhost:8080/login")).POST(HttpRequest.BodyPublishers.ofString("foo")).build(), ofString());

        assertThat(correctLogin, hasStatus(200));
        assertThat(badLogin, hasStatus(500));
    }

    @Test
    public void when_url_contains_parameter_it_should_be_added_us_a_separate_condition() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?user=john")
                .doReturnStatus(400);
        httpClientMock.onPost("/login?user=john&pass=abc")
                .doReturnStatus(200);

//        HttpResponse notFound = httpClientMock.execute(new HttpPost("http://localhost/login"));
//        HttpResponse notFound_2 = httpClientMock.execute(new HttpPost("http://localhost/login?user=john&pass=abc&foo=bar"));

        final var wrong = httpClientMock.send(newBuilder(URI.create("http://localhost/login?user=john")).POST(noBody()).build(), ofString());
        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost/login?user=john&pass=abc")).POST(noBody()).build(), ofString());

        //assertThat(notFound, hasStatus(404));
        assertThat(wrong, hasStatus(400));
        assertThat(ok, hasStatus(200));
        //assertThat(notFound_2, hasStatus(404));
    }

    @Test
    public void when_url_contains_reference_it_should_be_added_as_a_separate_condition() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .doReturnStatus(400);
        httpClientMock.onPost("/login#abc")
                .doReturnStatus(200);

        //HttpResponse wrong = httpClientMock.execute(new HttpPost("http://localhost/login"));
        //HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login#abc"));

        final var wrong = httpClientMock.send(newBuilder(URI.create("http://localhost/login")).POST(noBody()).build(), ofString());
        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost/login#abc")).POST(noBody()).build(), ofString());

        assertThat(wrong, hasStatus(400));
        assertThat(ok, hasStatus(200));
    }

    @Test
    public void should_handle_path_with_parameters_and_reference() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?p=1#abc")
                .doReturnStatus(200);

        // TODO: Check for exceptions on wrong cases once it's implemented

//        HttpResponse wrong1 = httpClientMock.execute(new HttpPost("http://localhost/login"));
//        HttpResponse wrong2 = httpClientMock.execute(new HttpPost("http://localhost/login?p=1"));
//        HttpResponse wrong3 = httpClientMock.execute(new HttpPost("http://localhost/login#abc"));
//        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login?p=1#abc"));

        final var ok = httpClientMock.send(newBuilder(URI.create("http://localhost/login?p=1#abc")).POST(noBody()).build(), ofString());

//        assertThat(wrong1, hasStatus(404));
//        assertThat(wrong2, hasStatus(404));
//        assertThat(wrong3, hasStatus(404));
        assertThat(ok, hasStatus(200));
    }
}
