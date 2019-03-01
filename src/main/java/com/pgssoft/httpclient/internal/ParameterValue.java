package com.pgssoft.httpclient.internal;

import java.util.List;

class ParameterValue {

    final String name;
    final List<String> values;

    public ParameterValue(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }
}
