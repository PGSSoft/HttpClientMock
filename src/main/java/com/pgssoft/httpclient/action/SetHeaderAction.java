package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.Action;
import com.pgssoft.httpclient.MockedServerResponse;


public final class SetHeaderAction implements Action {

    private final String key, value;

    public SetHeaderAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) {
        responseBuilder.addHeader(key, value);
    }
}
