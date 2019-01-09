package com.pgssoft;

import com.pgssoft.matchers.MatchersList;
import com.pgssoft.matchers.MatchersMap;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class UrlConditions {

    private static final int EMPTY_PORT = -1;
    private MatchersMap<String, String> parameterConditions = new MatchersMap<>();
    private Matcher<String> referenceConditions = Matchers.isEmptyOrNullString();
    private MatchersList<String> hostConditions = new MatchersList<>();
    private MatchersList<String> pathConditions = new MatchersList<>();
    private MatchersList<Integer> portConditions = new MatchersList<>();
    private Matcher<String> schemaConditions = Matchers.any(String.class);

    public static UrlConditions parse(String urlText) {
        try {
            UrlConditions conditions = new UrlConditions();
            URL url = new URL(urlText);
            if (url.getRef() != null) {
                conditions.setReferenceConditions(equalTo(url.getRef()));
            } else {
                conditions.setReferenceConditions(isEmptyOrNullString());
            }
            conditions.setSchemaConditions(Matchers.equalTo(url.getProtocol()));
            if (url.getHost() != null && url.getHost().length() > 0) {  // TODO: Could use a better way of checking for null strings in this method
                conditions.getHostConditions().add(equalTo(url.getHost()));
            }
            if (url.getPort() != -1) {
                conditions.getPortConditions().add(equalTo(url.getPort()));
            }
            if (url.getPath() != null && url.getPath().length() > 0) {
                conditions.getPathConditions().add(equalTo(url.getPath()));
            }
            List<KeyValuePair> params = UrlParams.parse(url.getQuery(), Charset.forName("UTF-8"));
            for (KeyValuePair param : params) {
                conditions.getParameterConditions().put(param.getKey(), equalTo(param.getValue()));
            }
            return conditions;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MatchersMap<String, String> getParameterConditions() {
        return parameterConditions;
    }

    public Matcher<String> getReferenceConditions() {
        return referenceConditions;
    }

    public void setReferenceConditions(Matcher<String> referenceConditions) {
        this.referenceConditions = referenceConditions;
    }

    public MatchersList<String> getHostConditions() {
        return hostConditions;
    }

    public void setHostConditions(MatchersList<String> hostConditions) {
        this.hostConditions = hostConditions;
    }

    public MatchersList<String> getPathConditions() {
        return pathConditions;
    }

    public MatchersList<Integer> getPortConditions() {
        return portConditions;
    }

    public void setSchemaConditions(Matcher<String> schemaConditions) {
        this.schemaConditions = schemaConditions;
    }

    public boolean matches(URI uri) {
        try {
            URL url = uri.toURL();

            return hostConditions.allMatches(url.getHost())
                    && pathConditions.allMatches(url.getPath())
                    && portConditions.allMatches(url.getPort())
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
        return params.stream()
                .allMatch(param -> parameterConditions.matches(param.getKey(), param.getValue()));
    }

    private Set<String> findMissingParameters(String query) {
        UrlParams params = UrlParams.parse(query);
        return parameterConditions.keySet().stream()
                .filter(((Predicate<String>) params::contain).negate())
                .collect(Collectors.toSet());
    }

    public void join(UrlConditions a) {
        this.referenceConditions = a.referenceConditions;
        this.schemaConditions = a.schemaConditions;
        this.portConditions.addAll(a.portConditions);
        this.pathConditions.addAll(a.pathConditions);
        this.hostConditions.addAll(a.hostConditions);
        for (String paramName : a.parameterConditions.keySet()) {
            for (Matcher<String> paramValue : a.parameterConditions.get(paramName)) {
                this.parameterConditions.put(paramName, paramValue);
            }
        }
    }

    private String describe(Matcher<String> matcher) {
        return StringDescription.toString(matcher);
    }

    private String portDebugDescription() {
        if (portConditions.allMatches(EMPTY_PORT)) {
            return "empty";
        } else {
            return portConditions.describe();
        }
    }
}
