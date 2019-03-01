package com.pgssoft.httpclient;

import java.net.http.HttpRequest;

class NoMatchingRuleException extends IllegalStateException {
    NoMatchingRuleException(HttpRequest request) {
        super("No rule found for request: [" + request.method() + ": " + request.uri() + "]");
    }
}
