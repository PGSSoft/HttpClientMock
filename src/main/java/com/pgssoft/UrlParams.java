package com.pgssoft;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

class UrlParams extends ArrayList<KeyValuePair> {

    static UrlParams parse(String query) {
        return parse(query, Charset.forName("UTF-8"));
    }

    static UrlParams parse(String query, Charset charset) {
        if (query == null) {
            return new UrlParams();
        } else {
            UrlParams urlParams = new UrlParams();
            splitQuery(query).forEach((k, v) -> {
                v.forEach(str -> urlParams.add(new KeyValuePair(k, str)));
            });
            return urlParams;
        }
    }

    boolean contain(String name) {
        return stream().anyMatch(p -> p.getKey().equals(name));
    }

    // TODO: Custom parsing logic, needs to be heavily tested or replaced with a trusted third party dependency
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
}
