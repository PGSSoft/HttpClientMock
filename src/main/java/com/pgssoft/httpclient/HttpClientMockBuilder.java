package com.pgssoft.httpclient;

import com.pgssoft.httpclient.internal.condition.BodyCondition;
import com.pgssoft.httpclient.internal.condition.HeaderCondition;
import com.pgssoft.httpclient.internal.rule.RuleBuilder;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public final class HttpClientMockBuilder {

    private final RuleBuilder ruleBuilder;
    private final HttpClientResponseBuilder responseBuilder;

    HttpClientMockBuilder(RuleBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
        this.responseBuilder = new HttpClientResponseBuilder(ruleBuilder);
    }

    /**
     * Adds header condition. Header must be equal to provided value.
     *
     * @param header header name
     * @param value  expected value
     * @return condition builder
     */
    public HttpClientMockBuilder withHeader(String header, String value) {
        Objects.requireNonNull(header, "header must be not null");
        return withHeader(header, Matchers.equalTo(value));
    }

    /**
     * Adds header condition. Header must be equal to provided value.
     *
     * @param header  header name
     * @param matcher header value matcher
     * @return condition builder
     */
    public HttpClientMockBuilder withHeader(String header, Matcher<String> matcher) {
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
    public HttpClientMockBuilder withReference(String reference) {
        Objects.requireNonNull(reference, "reference must be not null");
        return withReference(Matchers.equalTo(reference));
    }

    /**
     * Adds reference condition. Reference must match.
     *
     * @param matcher reference matcher
     * @return conditions builder
     */
    public HttpClientMockBuilder withReference(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setReferenceCondition(matcher);
        return this;
    }

    /**
     * Adds parameter condition. Parameter must be equal to provided value.
     *
     * @param name  parameter name
     * @param value expected parameter value
     * @return condition builder
     */
    public HttpClientMockBuilder withParameter(String name, String value) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(value, "value must be not null");
        return withParameter(name, Matchers.equalTo(value));
    }

    /**
     * Adds parameter condition. Parameter value must match.
     *
     * @param name    parameter name
     * @param matcher parameter value matcher
     * @return condition builder
     */
    public HttpClientMockBuilder withParameter(String name, Matcher<String> matcher) {
        Objects.requireNonNull(name, "name must be not null");
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setParameterCondition(name, matcher);
        return this;
    }

    /**
     * Adds custom conditions.
     *
     * @param condition custom condition
     * @return condition builder
     */
    public HttpClientMockBuilder with(Condition condition) {
        Objects.requireNonNull(condition, "condition must be not null");
        ruleBuilder.addCondition(condition);
        return this;
    }

    /**
     * Adds body condition. Request body must match provided matcher.
     *
     * @param matcher custom condition
     * @return condition builder
     */
    public HttpClientMockBuilder withBody(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.addCondition(new BodyCondition(matcher));
        return this;
    }

    /**
     * Adds host condition. Request host must be equal to provided value.
     *
     * @param host expected host
     * @return condition builder
     */
    public HttpClientMockBuilder withHost(String host) {
        Objects.requireNonNull(host, "host must be not null");
        ruleBuilder.addHostCondition(host);
        return this;
    }

    /**
     * Adds path condition. Request path must be equal to provided value.
     *
     * @param path expected path
     * @return condition builder
     */
    public HttpClientMockBuilder withPath(String path) {
        Objects.requireNonNull(path, "path must be not null");
        return withPath(Matchers.equalTo(path));
    }

    /**
     * Adds path condition. Request path must match.
     *
     * @param matcher path matcher
     * @return condition builder
     */
    public HttpClientMockBuilder withPath(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher must be not null");
        ruleBuilder.setPathCondition(matcher);
        return this;
    }

    /**
     * Adds custom action.
     *
     * @param action custom action
     * @return response builder
     */
    public HttpClientResponseBuilder doAction(Action action) {
        Objects.requireNonNull(action, "action must be not null");
        return responseBuilder.doAction(action);
    }

    /**
     * Adds action which returns provided response in UTF-8 and status 200.
     *
     * @param response response to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturn(String response) {
        Objects.requireNonNull(response, "response must be not null");
        return responseBuilder.doReturn(response);
    }

    /**
     * Adds action which returns provided response and status in UTF-8.
     *
     * @param statusCode status to return
     * @param response   response to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturn(int statusCode, String response) {
        Objects.requireNonNull(response, "response must be not null");
        return responseBuilder.doReturn(statusCode, response);
    }

    /**
     * Adds action which returns provided response in provided charset and status 200.
     *
     * @param response response to return
     * @param charset  charset to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturn(String response, Charset charset) {
        Objects.requireNonNull(response, "response must be not null");
        Objects.requireNonNull(charset, "charset must be not null");
        return responseBuilder.doReturn(response, charset);
    }

    /**
     * Adds action which returns empty message and provided status.
     *
     * @param statusCode status to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnStatus(int statusCode) {
        return responseBuilder.doReturnStatus(statusCode);
    }

    /**
     * Adds action which throws provided exception.
     *
     * @param exception exception to be thrown
     * @return response builder
     */
    public HttpClientResponseBuilder doThrowException(IOException exception) {
        Objects.requireNonNull(exception, "exception must be not null");
        return responseBuilder.doThrowException(exception);
    }

    /**
     * Adds action which returns provided JSON in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/json".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnJSON(String response) {
        Objects.requireNonNull(response, "response must be not null");
        return responseBuilder.doReturnJSON(response);
    }

    /**
     * Adds action which returns provided JSON in provided encoding and status 200. Additionally it sets "Content-type" header to "application/json".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
        Objects.requireNonNull(response, "response must be not null");
        Objects.requireNonNull(charset, "charset must be not null");
        return responseBuilder.doReturnJSON(response, charset);
    }

    /**
     * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnXML(String response) {
        Objects.requireNonNull(response, "response must be not null");
        return responseBuilder.doReturnXML(response);
    }

    /**
     * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
        Objects.requireNonNull(response, "response must be not null");
        Objects.requireNonNull(charset, "charset must be not null");
        return responseBuilder.doReturnXML(response, charset);
    }
}
