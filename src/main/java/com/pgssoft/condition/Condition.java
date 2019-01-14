package com.pgssoft.condition;

import com.pgssoft.debug.Debugger;

import java.net.http.HttpRequest;

public interface Condition {
    boolean matches(HttpRequest request);

    default void debug(HttpRequest request, Debugger debugger) {
        debugger.message(matches(request), getClass().getSimpleName());
    }
}
