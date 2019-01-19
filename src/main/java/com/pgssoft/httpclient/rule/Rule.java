package com.pgssoft.httpclient.rule;

import com.pgssoft.httpclient.MockedServerResponse;
import com.pgssoft.httpclient.internal.UrlConditions;
import com.pgssoft.httpclient.Action;
import com.pgssoft.httpclient.action.ActionBundle;
import com.pgssoft.httpclient.Condition;
import com.pgssoft.httpclient.debug.Debugger;

import java.io.IOException;
import java.net.http.HttpRequest;
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

    public MockedServerResponse produceResponse() throws IOException  {
        final var responseBuilder = new MockedServerResponse.Builder();

        final var actionBundle = actionBundles.size() > 1 ? actionBundles.poll() : actionBundles.peek();
        for (Action a : actionBundle) {
            a.enrichResponse(responseBuilder);
        }

        return responseBuilder.build();
    }

    public void debug(HttpRequest request, Debugger debugger) {
        for (Condition condition : conditions) {
            condition.debug(request, debugger);
        }
        urlConditions.debug(request, debugger);
    }
}
