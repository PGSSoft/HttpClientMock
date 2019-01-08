package com.pgssoft.rule;

import com.pgssoft.UrlConditions;
import com.pgssoft.action.Action;
import com.pgssoft.condition.Condition;
import com.pgssoft.condition.MethodCondition;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class RuleBuilder {

    private final Queue<Action> actions = new LinkedList<>();
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
        actions.add(action);
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

    public Action getLastAction() {
        return actions.peek();
    }

    public Rule build() {
        return new Rule(urlConditions, conditions, actions);
    }
}
