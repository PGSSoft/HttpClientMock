package com.pgssoft.condition;

import org.hamcrest.Matcher;

import java.net.http.HttpRequest;
import java.util.Objects;

public final class HeaderCondition implements Condition {

    private final String header;
    private final Matcher<String> expectedValue;

    public HeaderCondition(String header, Matcher<String> expectedValue) {
        this.header = header;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean matches(HttpRequest request) {
        return request.headers().allValues(header)
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(expectedValue::matches);
    }
}
