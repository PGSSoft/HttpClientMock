package com.pgssoft.httpclient.internal;

public final class KeyValuePair {

    private final String key, value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
