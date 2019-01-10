package com.pgssoft.condition;

import com.pgssoft.PeekSubscriber;
import org.hamcrest.Matcher;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public final class BodyCondition implements Condition {

    private final Matcher<String> matcher;

    public BodyCondition(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(HttpRequest request) {
        final var subscriber = new PeekSubscriber();
        request.bodyPublisher().orElseThrow().subscribe(subscriber);
        final var content = subscriber.content();
        return content != null && matcher.matches(new String(content.array(), StandardCharsets.UTF_8));
    }
}
