package com.pgssoft.httpclient.internal;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UrlParamsMatcher {

    private Map<String, Matcher<Iterable<? extends String>>> matchers;

    public UrlParamsMatcher(UrlParams params) {
        matchers = new HashMap<>();
        for (ParameterValue param : params.getParams()) {
            String[] values = param.getValues().toArray(new String[]{});
            matchers.put(param.getName(), Matchers.containsInAnyOrder(values));
        }
    }

    public UrlParamsMatcher() {
    }


    private boolean setsOfParametersAreEqual(String query) {
        Set<String> expectedParams = matchers.keySet();
        Set<String> actualParams = UrlParams.parse(query).getNames();
        return expectedParams.equals(actualParams);
    }

    private boolean allParamsHaveMatchingValue(String query) {
        UrlParams params = UrlParams.parse(query);
        return params.getParams().stream()
                .allMatch(param -> matchers.get(param.getName()).matches(param.getValues()));
    }


    public boolean matches(String query) {
        return noMatchersWereDefined() ||
                (setsOfParametersAreEqual(query) && allParamsHaveMatchingValue(query));
    }

    private boolean noMatchersWereDefined() {
        return matchers == null;
    }

    public void addParam(String name, Matcher<Iterable<? extends String>> matcher) {
        if (noMatchersWereDefined()) {
            matchers = new HashMap<>();
        }
        matchers.put(name, matcher);
    }
}
