package com.pgssoft.httpclient.internal;

import com.pgssoft.httpclient.debug.Debugger;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import static org.hamcrest.Matchers.equalTo;

public class UrlConditions {

    private static final int EMPTY_PORT = -1;
    private UrlParamsMatcher parameterConditions = new UrlParamsMatcher();
    private Matcher<String> referenceCondition = Matchers.anyOf(Matchers.any(String.class), Matchers.nullValue());
    private Matcher<String> hostCondition = Matchers.anyOf(Matchers.any(String.class), Matchers.nullValue());
    private Matcher<String> pathCondition = Matchers.anyOf(Matchers.any(String.class), Matchers.nullValue());
    private Matcher<Integer> portCondition = Matchers.any(Integer.class);
    private Matcher<String> schemaCondition = Matchers.anyOf(Matchers.any(String.class), Matchers.nullValue());

    public static UrlConditions parse(String urlText) {
        try {
            UrlConditions conditions = new UrlConditions();
            URI uri = new URI(urlText);
            UrlParams params = UrlParams.parse(uri.getQuery());
            conditions.setSchemaCondition(getStringMatcher(uri.getScheme()));
            conditions.setHostCondition(getStringMatcher(uri.getHost()));
            conditions.setPortCondition(equalTo(uri.getPort()));
            conditions.setPathCondition(getStringMatcher(uri.getPath()));
            conditions.setReferenceCondition(getStringMatcher(uri.getFragment()));
            conditions.setParameterConditions(new UrlParamsMatcher(params));
            return conditions;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private static Matcher<String> getStringMatcher(String val) {
        if (val == null || val.isEmpty()) {
            return Matchers.isEmptyOrNullString();
        } else {
            return Matchers.equalTo(val);
        }
    }

    public boolean matches(URI uri) {
        return hostCondition.matches(uri.getHost())
                && pathCondition.matches(uri.getPath())
                && portCondition.matches(uri.getPort())
                && referenceCondition.matches(uri.getFragment())
                && schemaCondition.matches(uri.getScheme())
                && parameterConditions.matches(uri.getQuery());

    }

    public void debug(HttpRequest request, Debugger debugger) {
        URI uri = request.uri();
        debugger.message(schemaCondition.matches(uri.getScheme()), "schema is " + describeMatcher(schemaCondition));
        debugger.message(hostCondition.matches(uri.getHost()), "host is " + describeMatcher(hostCondition));
        debugger.message(portCondition.matches(uri.getPort()), "port is " + portDebugDescription());
        debugger.message(pathCondition.matches(uri.getPath()), "path is " + describeMatcher(pathCondition));
        debugger.message(parameterConditions.matches(uri.getQuery()), "all URI parameters have matching value");
        debugger.message(referenceCondition.matches(uri.getFragment()), "URI reference has matching value");
    }

    private String portDebugDescription() {
        if (portCondition.matches(EMPTY_PORT)) {
            return "empty";
        } else {
            return describeMatcher(portCondition);
        }
    }

    private String describeMatcher(Matcher<?> matcher) {
        return StringDescription.toString(matcher);
    }


    public UrlParamsMatcher getParameterConditions() {
        return parameterConditions;
    }

    public void setParameterConditions(UrlParamsMatcher parameterConditions) {
        this.parameterConditions = parameterConditions;
    }

    public Matcher<String> getReferenceCondition() {
        return referenceCondition;
    }

    public void setReferenceCondition(Matcher<String> referenceCondition) {
        this.referenceCondition = referenceCondition;
    }

    public Matcher<String> getHostCondition() {
        return hostCondition;
    }

    public void setHostCondition(Matcher<String> hostCondition) {
        this.hostCondition = hostCondition;
    }

    public Matcher<String> getPathCondition() {
        return pathCondition;
    }

    public void setPathCondition(Matcher<String> pathCondition) {
        this.pathCondition = pathCondition;
    }

    public Matcher<Integer> getPortCondition() {
        return portCondition;
    }

    public void setPortCondition(Matcher<Integer> portCondition) {
        this.portCondition = portCondition;
    }

    public Matcher<String> getSchemaCondition() {
        return schemaCondition;
    }

    public void setSchemaCondition(Matcher<String> schemaCondition) {
        this.schemaCondition = schemaCondition;
    }
}
