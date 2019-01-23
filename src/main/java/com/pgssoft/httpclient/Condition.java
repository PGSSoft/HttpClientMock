package com.pgssoft.httpclient;

import java.net.http.HttpRequest;

public interface Condition {
    boolean matches(HttpRequest request);

    default String getDebugMessage() {
        return getClass().getSimpleName();
    }

}
