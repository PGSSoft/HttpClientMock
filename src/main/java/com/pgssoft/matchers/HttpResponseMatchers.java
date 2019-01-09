package com.pgssoft.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.net.http.HttpResponse;

public final class HttpResponseMatchers {

    public static Matcher<? super HttpResponse> hasStatus(int expectedStatus) {
        return new BaseMatcher<HttpResponse>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof HttpResponse)) {
                    return false;
                }

                return ((HttpResponse) o).statusCode() == expectedStatus;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(expectedStatus);
            }
        };
    }

    public static Matcher<? super HttpResponse> hasContent(final String content) {
        return new BaseMatcher<HttpResponse>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof HttpResponse)) {
                    return false;
                }

                final HttpResponse<String> response = (HttpResponse<String>) o;
                return response.body() != null && response.body().equals(content);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(content);
            }
        };
    }
}
