package com.pgssoft;

import com.pgssoft.action.*;
import com.pgssoft.rule.RuleBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class HttpClientResponseBuilder {

    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_XML = "application/xml";

    private final RuleBuilder ruleBuilder;

    HttpClientResponseBuilder(RuleBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
    }

    public HttpClientResponseBuilder withHeader(String name, String value) {
        return doAction(new SetHeaderAction(name, value));
    }

    public HttpClientResponseBuilder withStatus(int statusCode) {
        return doAction(new SetStatusAction(statusCode));
    }

    public HttpClientResponseBuilder doAction(Action action) {
        ruleBuilder.addAction(action);
        return this;
    }

    public HttpClientResponseBuilder doReturn(String response) {
        return doReturn(response, StandardCharsets.UTF_8);
    }

    public HttpClientResponseBuilder doReturn(int statusCode, String response) {
        return doReturn(statusCode, response, StandardCharsets.UTF_8);
    }

    public HttpClientResponseBuilder doReturn(String response, Charset charset) {
        return doReturn(200, response, charset);
    }

    public HttpClientResponseBuilder doReturn(int statusCode, String response, Charset charset) {
        //ruleBuilder.addAction(new SetBodyStringAction(statusCode, response)); // TODO: Include charset and statusCode
        ruleBuilder.addActionBundle(new SetBodyStringAction(response));
        ruleBuilder.addAction(new SetStatusAction(statusCode));
        return new HttpClientResponseBuilder(ruleBuilder);
    }

    public HttpClientResponseBuilder doReturnStatus(int statusCode) {
        ruleBuilder.addActionBundle(new SetStatusAction(statusCode));
        return new HttpClientResponseBuilder(ruleBuilder);
    }

    public HttpClientResponseBuilder doThrowException(IOException exception) {
        ruleBuilder.addActionBundle(new ThrowExceptionAction(exception));
        return new HttpClientResponseBuilder(ruleBuilder);
    }

    public HttpClientResponseBuilder doReturnJSON(String response) {
        return doReturnJSON(response, StandardCharsets.UTF_8);
    }

    public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_JSON);
    }

    public HttpClientResponseBuilder doReturnXML(String response) {
        return doReturnXML(response, StandardCharsets.UTF_8);
    }

    public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_XML);
    }
}
