package com.pgssoft.httpclient.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.HashMap;

public class MatchersMap<K, V> extends HashMap<K, Matcher<V>> {

    public boolean matches(K name, V value) {
        return this.containsKey(name) && this.get(name).matches(value);
    }

    public Matcher<V> put(K name, Matcher<V> value) {
        return this.putIfAbsent(name, value);
    }

    public String describe(String name) {
        return StringDescription.toString(get(name));
    }
}
