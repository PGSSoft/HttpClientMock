package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.HttpResponseProxy;

public final class SetHeaderAction implements Action {

    private final String key, value;

    public SetHeaderAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) {
        responseBuilder.addHeader(key, value);
    }
}
