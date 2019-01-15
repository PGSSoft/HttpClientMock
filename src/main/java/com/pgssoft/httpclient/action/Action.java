package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.HttpResponseProxy;

import java.io.IOException;

public interface Action {
    void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws IOException;
}
