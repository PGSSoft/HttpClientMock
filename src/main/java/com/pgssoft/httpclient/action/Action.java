package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.MockedServerResponse;

import java.io.IOException;

public interface Action {
    void enrichResponse(MockedServerResponse.Builder responseBuilder) throws IOException;
}
