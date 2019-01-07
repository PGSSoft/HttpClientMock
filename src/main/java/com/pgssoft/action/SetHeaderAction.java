package com.pgssoft.action;

import com.pgssoft.MockResponseBuilder;

public final class SetHeaderAction implements Action {

    private final String key, value;

    public SetHeaderAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void enrichResponse(MockResponseBuilder responseBuilder) {
        responseBuilder.addHeader(key, value);
    }
}
