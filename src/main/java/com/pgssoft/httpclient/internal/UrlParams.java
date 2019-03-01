package com.pgssoft.httpclient.internal;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

class UrlParams {

    private final ArrayList<ParameterValue> params = new ArrayList<>();

    boolean contains(String name) {
        return params.stream().anyMatch(p -> p.getName().equals(name));
    }

    public List<ParameterValue> getParams() {
        return params;
    }


    static UrlParams parse(String query) {
        if (query == null) {
            return new UrlParams();
        } else {
            UrlParams urlParams = new UrlParams();
            splitQuery(query).forEach((k, v) -> urlParams.params.add(new ParameterValue(k, v)));
            return urlParams;
        }
    }


    private static Map<String, List<String>> splitQuery(String query) {
        if (query == null || query.length() <= 0) {
            return Collections.emptyMap();
        }

        return Arrays.stream(query.split("&"))
                .map(UrlParams::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public Set<String> getNames() {
        return params.stream().map(ParameterValue::getName).collect(Collectors.toSet());
    }
}
