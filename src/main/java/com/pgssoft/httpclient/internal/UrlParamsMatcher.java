package com.pgssoft.httpclient.internal;

import com.pgssoft.httpclient.debug.Debugger;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    private void printParameterDebugMessage(ParameterValue param, Debugger debugger) {
        if (matchers.containsKey(param.getName())) {
            boolean matches = matchers.get(param.getName()).matches(param.getValues());
            String message = "parameter " + param.getName() + " has matching value";
            debugger.message(matches, message);
        } else {
            String message = "parameter " + param.getName() + " is redundant";
            debugger.message(false, message);
        }
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
