package com.pgssoft.httpclient.internal.condition;

import com.pgssoft.httpclient.Condition;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

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

    @Override
    public String getDebugMessage() {
        return "header " + header + " is " + StringDescription.toString(expectedValue);
    }
}
