package com.pgssoft.rule;

import com.pgssoft.MockResponseBuilder;
import com.pgssoft.action.Action;
import com.pgssoft.condition.Condition;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;

public final class Rule {

    private final Queue<Action> actions;
    private final List<Condition> conditions;

    public Rule(List<Condition> conditions, Queue<Action> actions) {
        this.conditions = conditions;
        this.actions = actions;
    }

    public boolean matches(HttpRequest request) {
        return conditions.stream().allMatch(c -> c.matches(request));
    }

    public HttpResponse next() {
        final var responseBuilder = new MockResponseBuilder();
        for (int i = 0; i < actions.size()-1; i++) {
            actions.poll().enrichResponse(responseBuilder);
        }
        actions.peek().enrichResponse(responseBuilder);
        return responseBuilder.build();
    }
}
