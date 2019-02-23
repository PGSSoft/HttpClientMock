package com.pgssoft.httpclient;

import com.pgssoft.httpclient.internal.rule.RuleBuilder;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;

import static com.pgssoft.httpclient.internal.HttpMethods.*;

public final class HttpClientVerify {

    private final String defaultHost;
    private final List<HttpRequest> requests;

    HttpClientVerify(String defaultHost, List<HttpRequest> requests) {
        this.requests = requests;
        this.defaultHost = defaultHost;
    }

    private HttpClientVerifyBuilder newRule(String method) {
        RuleBuilder r = new RuleBuilder(method);
        return new HttpClientVerifyBuilder(r, requests);
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        RuleBuilder r = new RuleBuilder(method, defaultHost, url);
        return new HttpClientVerifyBuilder(r, requests);
    }

    public HttpClientVerifyBuilder post(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(POST, url);
    }

    public HttpClientVerifyBuilder get(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(GET, url);
    }

    public HttpClientVerifyBuilder put(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(PUT, url);
    }

    public HttpClientVerifyBuilder delete(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(DELETE, url);
    }

    public HttpClientVerifyBuilder head(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HEAD, url);
    }

    public HttpClientVerifyBuilder options(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(OPTIONS, url);
    }

    public HttpClientVerifyBuilder patch(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(PATCH, url);
    }

    public HttpClientVerifyBuilder post() {
        return newRule(POST);
    }

    public HttpClientVerifyBuilder get() {
        return newRule(GET);
    }

    public HttpClientVerifyBuilder put() {
        return newRule(PUT);
    }

    public HttpClientVerifyBuilder delete() {
        return newRule(DELETE);
    }

    public HttpClientVerifyBuilder head() {
        return newRule(HEAD);
    }

    public HttpClientVerifyBuilder options() {
        return newRule(OPTIONS);
    }

    public HttpClientVerifyBuilder patch() {
        return newRule(PATCH);
    }

}
