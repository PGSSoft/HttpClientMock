package com.pgssoft.httpclient.rule;

import com.pgssoft.httpclient.internal.UrlConditions;
import com.pgssoft.httpclient.action.Action;
import com.pgssoft.httpclient.action.ActionBundle;
import com.pgssoft.httpclient.condition.Condition;
import com.pgssoft.httpclient.condition.MethodCondition;
import org.hamcrest.Matcher;

import java.util.*;

public final class RuleBuilder {

    private final Deque<ActionBundle> actionBundles = new LinkedList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final UrlConditions urlConditions = new UrlConditions();

    public RuleBuilder(String method, String host, String url) {
        url = url.startsWith("/") ? host + url : url;
        addCondition(new MethodCondition(method));
        addUrlConditions(UrlConditions.parse(url));
    }

    public RuleBuilder(String method) {
        addCondition(new MethodCondition(method));
    }

    public void addAction(Action action) {
        var bundle = actionBundles.peekLast();
        if (bundle == null) {
            bundle = new ActionBundle();
            actionBundles.add(bundle);
        }
        bundle.add(action);
    }

    public void addActionBundle(Action action) {
        final var bundle = new ActionBundle();
        bundle.add(action);
        actionBundles.add(bundle);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    private void addUrlConditions(UrlConditions urlConditions) {
        this.urlConditions.join(urlConditions);
    }

    public void addParameterCondition(String name, Matcher<String> matcher) {
        final var conditions = new UrlConditions();
        conditions.getParameterConditions().put(name, matcher);
        addUrlConditions(conditions);
    }

    public void addReferenceCondition(Matcher<String> matcher) {
        final var conditions = new UrlConditions();
        conditions.setReferenceConditions(matcher);
        addUrlConditions(conditions);
    }

    public void addHostCondition(String host) {
        addUrlConditions(UrlConditions.parse(host));
    }

    public void addPathCondition(Matcher<String> matcher) {
        final var conditions = new UrlConditions();
        conditions.getPathConditions().add(matcher);
        addUrlConditions(conditions);
    }

    public Rule build() {
        return new Rule(urlConditions, conditions, actionBundles);
    }
}
