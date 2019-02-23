package com.pgssoft.httpclient;

import com.pgssoft.httpclient.internal.condition.BodyCondition;
import com.pgssoft.httpclient.internal.condition.HeaderCondition;
import com.pgssoft.httpclient.internal.rule.Rule;
import com.pgssoft.httpclient.internal.rule.RuleBuilder;
import org.hamcrest.Matcher;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;

public final class HttpClientVerifyBuilder {

    private final RuleBuilder ruleBuilder;
    private final List<HttpRequest> requests;

    HttpClientVerifyBuilder(RuleBuilder ruleBuilder, List<HttpRequest> requests) {
        this.requests = requests;
        this.ruleBuilder = ruleBuilder;
    }

    /**
     * Adds header condition. Header must be equal to provided value.
     *
     * @param header header name
     * @param value  expected value
     * @return verification builder
     */
    public HttpClientVerifyBuilder withHeader(String header, String value) {
        Objects.requireNonNull(header, "header must be not null");
        return withHeader(header, equalTo(value));
    }

    /**
     * Adds header condition. Header must be equal to provided value.
     *
     * @param header  header name
     * @param matcher header value matcher
     * @return verification builder
     */
    public HttpClientVerifyBuilder withHeader(String header, Matcher<String> matcher) {
        Objects.requireNonNull(header, "header must be not null");
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.addCondition(new HeaderCondition(header, matcher));
        return this;
    }

    /**
     * Adds reference condition. Reference must be equal to provided value.
     *
     * @param reference expected reference
     * @return conditions builder
     */
    public HttpClientVerifyBuilder withReference(String reference) {
        Objects.requireNonNull(reference, "reference must be not null");
        return withReference(equalTo(reference));
    }

    /**
     * Adds reference condition. Reference must match.
     *
     * @param matcher reference matcher
     * @return conditions builder
     */
    public HttpClientVerifyBuilder withReference(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setReferenceCondition(matcher);
        return this;
    }

    /**
     * Adds parameter condition. Parameter must be equal to provided value.
     *
     * @param name  parameter name
     * @param value expected parameter value
     * @return verification builder
     */
    public HttpClientVerifyBuilder withParameter(String name, String value) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(value, "value must be not null");
        return withParameter(name, equalTo(value));
    }

    /**
     * Adds parameter condition. Parameter value must match.
     *
     * @param name    parameter name
     * @param matcher parameter value matcher
     * @return verification builder
     */
    public HttpClientVerifyBuilder withParameter(String name, Matcher<String> matcher) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setParameterCondition(name, matcher);
        return this;
    }

    /**
     * Adds custom conditions.
     *
     * @param condition custom condition
     * @return verification builder
     */
    public HttpClientVerifyBuilder with(Condition condition) {
        Objects.requireNonNull(condition, "condition must be not null");
        ruleBuilder.addCondition(condition);
        return this;
    }

    /**
     * Adds body condition. Request body must match provided matcher.
     *
     * @param matcher custom condition
     * @return verification builder
     */
    public HttpClientVerifyBuilder withBody(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.addCondition(new BodyCondition(matcher));
        return this;
    }

    /**
     * Adds host condition. Request host must be equal to provided value.
     *
     * @param host expected host
     * @return verification builder
     */
    public HttpClientVerifyBuilder withHost(String host) {
        Objects.requireNonNull(host, "host must be not null");
        ruleBuilder.addHostCondition(host);
        return this;
    }

    /**
     * Adds path condition. Request path must be equal to provided value.
     *
     * @param path expected path
     * @return verification builder
     */
    public HttpClientVerifyBuilder withPath(String path) {
        Objects.requireNonNull(path, "path must be not null");
        return withPath(equalTo(path));
    }

    /**
     * Adds path condition. Request path must match.
     *
     * @param matcher path matcher
     * @return verification builder
     */
    public HttpClientVerifyBuilder withPath(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setPathCondition(matcher);
        return this;
    }

    /**
     * Verifies if there were no request matching defined conditions.
     */
    public void notCalled() {
        called(0);
    }

    /**
     * Verifies if there was exactly one request matching defined conditions.
     */
    public void called() {
        called(1);
    }

    /**
     * Verifies number of request matching defined conditions.
     *
     * @param numberOfCalls expected number of calls
     */
    public void called(int numberOfCalls) {
        called(equalTo(numberOfCalls));
    }

    /**
     * Verifies number of request matching defined conditions.
     *
     * @param numberOfCalls expected number of calls
     */
    public void called(Matcher<Integer> numberOfCalls) {
        Rule rule = ruleBuilder.build();
        int matchingCalls = (int)requests.stream()
                .filter(rule::matches)
                .count();
        if (!numberOfCalls.matches(matchingCalls)) {
            throw new IllegalStateException(String.format("Expected %s calls, but found %s.", numberOfCalls, matchingCalls));
        }
    }
}
