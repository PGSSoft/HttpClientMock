package com.pgssoft.condition;

import com.pgssoft.debug.Debugger;

import java.net.http.HttpRequest;

public final class MethodCondition implements Condition {

    private final String method;

    public MethodCondition(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(HttpRequest request) {
        return request.method().equals(method);
    }

    @Override
    public void debug(HttpRequest request, Debugger debugger) {
        debugger.message(matches(request), "HTTP method is " + method);
    }
}
