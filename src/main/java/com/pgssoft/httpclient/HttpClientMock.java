package com.pgssoft.httpclient;

import com.pgssoft.httpclient.debug.Debugger;
import com.pgssoft.httpclient.rule.RuleBuilder;
import com.pgssoft.httpclient.rule.Rule;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public final class HttpClientMock extends HttpClient {

    private final Debugger debugger;
    private final List<Rule> rules = new ArrayList<>();
    private final List<RuleBuilder> rulesUnderConstruction = new ArrayList<>();
    private final String host;
    private final List<HttpRequest> requests = new ArrayList<>();

    private boolean debuggingOn;

    public HttpClientMock() {
        this("");
    }

    /**
     * Creates mock of HttpClient with default host. All defined conditions without host will use default host
     *
     * @param host default host for later conditions
     */
    public HttpClientMock(String host) {
        this.host = host;
        this.debugger = new Debugger();
    }

    /**
     * Creates mock of HttpClient with default host. All defined conditions without host will use default host
     *
     * @param host default host for later conditions
     * @param debugger    debugger used for testing
     */
    public HttpClientMock(String host, Debugger debugger) {
        this.host = host;
        this.debugger = debugger;
    }

    /**
     * Resets mock to initial state where there are no rules and no previous requests.
     */
    public void reset() {
        this.rulesUnderConstruction.clear();
        this.requests.clear();
    }

    /**
     * Creates verification builder.
     *
     * @return request number verification builder
     */
    public HttpClientVerify verify() {
        return new HttpClientVerify(host, requests);
    }

    /**
     * Starts defining new rule which requires HTTP POST method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPost() {
        return newRule("POST");
    }

    /**
     * Starts defining new rule which requires HTTP GET method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onGet() {
        return newRule("GET");
    }

    /**
     * Starts defining new rule which requires HTTP DELETE method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onDelete() {
        return newRule("DELETE");
    }

    /**
     * Starts defining new rule which requires HTTP HEAD method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onHead() {
        return newRule("HEAD");
    }

    /**
     * Starts defining new rule which requires HTTP OPTION method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onOption() {
        return newRule("OPTION");
    }

    /**
     * Starts defining new rule which requires HTTP PUT method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPut() {
        return newRule("PUT");
    }

    /**
     * Starts defining new rule which requires HTTP PATCH method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPatch() {
        return newRule("PATCH");
    }

    /**
     * Starts defining new rule which requires HTTP GET method and url. If provided url starts with "/" request url must be equal to concatenation of default
     * host and url. Otherwise request url must equal to provided url. If provided url contains query parameters and/or reference they are parsed and added as a
     * separate conditions. <p> For example:<br> <code> httpClientMock.onGet("http://localhost/login?user=Ben#edit"); </code> <br>is equal to<br> <code>
     * httpClientMock.onGet("http://localhost/login").withParameter("user","Ben").withReference("edit); </code>
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onGet(String url) {
        return newRule("GET", url);
    }

    /**
     * Starts defining new rule which requires HTTP POST method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPost(String url) {
        return newRule("POST", url);
    }

    /**
     * Starts defining new rule which requires HTTP PUT method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPut(String url) {
        return newRule("PUT", url);
    }

    /**
     * Starts defining new rule which requires HTTP DELETE method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onDelete(String url) {
        return newRule("DELETE", url);
    }

    /**
     * Starts defining new rule which requires HTTP HEAD method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onHead(String url) {
        return newRule("HEAD", url);
    }

    /**
     * Starts defining new rule which requires HTTP OPTIONS method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onOptions(String url) {
        return newRule("OPTIONS", url);
    }

    /**
     * Starts defining new rule which requires HTTP PATCH method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPatch(String url) {
        return newRule("PATCH", url);
    }

    private HttpClientMockBuilder newRule(String method) {
        RuleBuilder r = new RuleBuilder(method);
        rulesUnderConstruction.add(r);
        return new HttpClientMockBuilder(r);
    }

    private HttpClientMockBuilder newRule(String method, String url) {
        RuleBuilder r = new RuleBuilder(method, host, url);
        rulesUnderConstruction.add(r);
        return new HttpClientMockBuilder(r);
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
        return null;
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
        return null;
    }

    @Override
    public SSLParameters sslParameters() {
        return null;
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return Optional.empty();
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public Optional<Executor> executor() {
        return Optional.empty();
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        synchronized (rulesUnderConstruction) {
            rules.addAll(
                    rulesUnderConstruction.stream()
                            .map(RuleBuilder::build)
                            .collect(Collectors.toList())
            );
            rulesUnderConstruction.clear();
        }

        requests.add(request);

        final Rule rule = rules.stream()
                .filter(r -> r.matches(request))
                .reduce((a, b) -> b)
                .orElse(null);

        if (debuggingOn || rule == null) {
            debugger.debug(rules, request);
        }

        return rule != null ? rule.next() : null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return null;
    }

    public void debugOn() {
        debuggingOn = true;
    }

    public void debugOff() {
        debuggingOn = false;
    }
}
