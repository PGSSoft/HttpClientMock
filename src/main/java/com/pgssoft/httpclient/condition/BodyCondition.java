package com.pgssoft.httpclient.condition;

import com.pgssoft.httpclient.Condition;
import com.pgssoft.httpclient.internal.PeekSubscriber;
import com.pgssoft.httpclient.debug.Debugger;
import org.hamcrest.Matcher;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class BodyCondition implements Condition {

    private final Matcher<String> matcher;

    public BodyCondition(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(HttpRequest request) {
        final Optional<HttpRequest.BodyPublisher> bodyPublisher = request.bodyPublisher();
        if (bodyPublisher.isEmpty()) {
            return matcher.matches(null);
        }

        final var subscriber = new PeekSubscriber();
        request.bodyPublisher().orElseThrow().subscribe(subscriber);
        final var content = subscriber.content();
        return content != null && matcher.matches(new String(content.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void debug(HttpRequest request, Debugger debugger) {
        debugger.message(matches(request), "body matches");
    }
}
