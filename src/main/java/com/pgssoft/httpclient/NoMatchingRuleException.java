package com.pgssoft.httpclient;

import java.net.http.HttpRequest;

public class NoMatchingRuleException extends IllegalStateException {
    public NoMatchingRuleException(HttpRequest request) {
        super("No rule found for request: [" + request.method() + ": " + request.uri() + "]");
    }
}
