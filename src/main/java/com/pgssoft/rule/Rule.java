package com.pgssoft.rule;

import com.pgssoft.HttpResponseProxy;
import com.pgssoft.UrlConditions;
import com.pgssoft.action.Action;
import com.pgssoft.action.ActionBundle;
import com.pgssoft.condition.Condition;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;

public final class Rule {

    private final UrlConditions urlConditions;
    private final List<Condition> conditions;
    private final Queue<ActionBundle> actionBundles;

    public Rule(UrlConditions urlConditions, List<Condition> conditions, Queue<ActionBundle> actionBundles) {
        this.urlConditions = urlConditions;
        this.conditions = conditions;
        this.actionBundles = actionBundles;
    }

    public boolean matches(HttpRequest request) {
        return urlConditions.matches(request.uri()) &&
                conditions.stream().allMatch(c -> c.matches(request));
    }

    public HttpResponse next() throws IOException {
        final var responseBuilder = new HttpResponseProxy.Builder();
        //final var action = actions.size() > 1 ? actions.poll() : actions.peek();
        //action.enrichResponse(responseBuilder);

        final var actionBundle = actionBundles.size() > 1 ? actionBundles.poll() : actionBundles.peek();
        for (Action a : actionBundle) {
            a.enrichResponse(responseBuilder);
        }

        return responseBuilder.build();
    }
}
