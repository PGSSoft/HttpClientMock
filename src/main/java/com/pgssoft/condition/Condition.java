package com.pgssoft.condition;

import java.net.http.HttpRequest;

public interface Condition {
    boolean matches(HttpRequest request);
}
