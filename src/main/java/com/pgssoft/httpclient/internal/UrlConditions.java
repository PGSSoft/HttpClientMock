package com.pgssoft.httpclient.internal;

import com.pgssoft.httpclient.debug.Debugger;
import com.pgssoft.httpclient.matchers.MatchersMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class UrlConditions {

    private static final int EMPTY_PORT = -1;
    private MatchersMap<String, Iterable<? extends String>> parameterConditions = new MatchersMap<>();
    private Matcher<String> referenceConditions = Matchers.isEmptyOrNullString();
    private Matcher<String> hostConditions = Matchers.any(String.class);
    private Matcher<String> pathConditions = Matchers.any(String.class);
    private Matcher<Integer> portConditions = Matchers.any(Integer.class);
    private Matcher<String> schemaConditions = Matchers.any(String.class);

    public static UrlConditions parse(String urlText) {
        try {
            UrlConditions conditions = new UrlConditions();
            URL url = new URL(urlText);
            conditions.setSchemaConditions(getStringMatcher(url.getProtocol()));
            conditions.setHostConditions(getStringMatcher(url.getHost()));
            conditions.setPortCondition(equalTo(url.getPort()));
            conditions.setPathCondition(getStringMatcher(url.getPath()));
            conditions.setReferenceConditions(getStringMatcher(url.getRef()));

            UrlParams params = UrlParams.parse(url.getQuery());
            for (ParameterValue param : params.getParams()) {
                String[] values = param.getValues().toArray(new String[]{});
                conditions.getParameterConditions().put(param.getName(), Matchers.containsInAnyOrder(values));
            }
            return conditions;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Matcher<String> getStringMatcher(String ref) {
        if (ref==null || ref.isEmpty()) {
            return Matchers.isEmptyOrNullString();
        } else {
            return Matchers.equalTo(ref);
        }
    }

    private void setPortCondition(Matcher<Integer> equalTo) {
        portConditions = equalTo;
    }

    public MatchersMap<String, Iterable<? extends  String>> getParameterConditions() {
        return parameterConditions;
    }

    public Matcher<String> getReferenceConditions() {
        return referenceConditions;
    }

    public void setReferenceConditions(Matcher<String> referenceConditions) {
        this.referenceConditions = referenceConditions;
    }

    public Matcher<String> getHostConditions() {
        return hostConditions;
    }

    public void setHostConditions(Matcher<String> hostConditions) {
        this.hostConditions = hostConditions;
    }

    public Matcher<String> getPathConditions() {
        return pathConditions;
    }

    public Matcher<Integer> getPortConditions() {
        return portConditions;
    }

    public void setSchemaConditions(Matcher<String> schemaConditions) {
        this.schemaConditions = schemaConditions;
    }

    public void setPathCondition(Matcher<String> matcher) {
        this.pathConditions = matcher;
    }

    public boolean matches(URI uri) {
        try {
            URL url = uri.toURL();

            return hostConditions.matches(url.getHost())
                    && pathConditions.matches(url.getPath())
                    && portConditions.matches(url.getPort())
                    && referenceConditions.matches(url.getRef())
                    && schemaConditions.matches(url.getProtocol())
                    && allDefinedParamsOccurredInURL(url.getQuery())
                    && allParamsHaveMatchingValue(url.getQuery());

        } catch (MalformedURLException e) {
            return false;
        }
    }

    private boolean allDefinedParamsOccurredInURL(String query) {
        return findMissingParameters(query).isEmpty();
    }

    private boolean allParamsHaveMatchingValue(String query) {
        UrlParams params = UrlParams.parse(query);
        return params.getParams().stream()
                .allMatch(param -> parameterConditions.matches(param.getName(), param.getValues()));
    }

    private Set<String> findMissingParameters(String query) {
        UrlParams params = UrlParams.parse(query);
        return parameterConditions.keySet().stream()
                .filter(((Predicate<String>) params::contain).negate())
                .collect(Collectors.toSet());
    }


    public void debug(HttpRequest request, Debugger debugger) {
        try {
            URL url = new URL(request.uri().toString());
            debugger.message(hostConditions.matches(url.getHost()), "schema is " + describe(schemaConditions));
            debugger.message(hostConditions.matches(url.getHost()), "host is " + StringDescription.toString(hostConditions));
            debugger.message(pathConditions.matches(url.getPath()), "path is " + StringDescription.toString(pathConditions));
            debugger.message(portConditions.matches(url.getPort()), "port is " + portDebugDescription());
            if (referenceConditions != isEmptyOrNullString() || !referenceConditions.matches(url.getRef())) {
                debugger.message(referenceConditions.matches(url.getRef()), "reference is " + describe(referenceConditions));
            }
            Set<String> missingParams = findMissingParameters(url.getQuery());
            for (String param : missingParams) {
                debugger.message(false, "parameter " + param + " occurs in request");
            }
            UrlParams params = UrlParams.parse(url.getQuery());
            params.getParams().forEach(param->printParameterDebugMessage(param,debugger));

        } catch (MalformedURLException e) {
            System.out.println("Can't parse URL: " + request.uri());
        }

    }

    private void printParameterDebugMessage(ParameterValue param, Debugger debugger) {
        if (parameterConditions.containsKey(param.getName())) {
            boolean matches = parameterConditions.matches(param.getName(), param.getValues());
            String message = "parameter " + param.getName() + " has matching value";
            debugger.message(matches, message);
        } else {
            String message = "parameter " + param.getName() + " is redundant";
            debugger.message(false, message);
        }
    }

    private String describe(Matcher<String> matcher) {
        return StringDescription.toString(matcher);
    }

    private String portDebugDescription() {
        if (portConditions.matches(EMPTY_PORT)) {
            return "empty";
        } else {
            return StringDescription.toString(portConditions);
        }
    }

}
