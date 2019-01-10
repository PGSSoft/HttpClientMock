package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

import java.io.IOException;

public interface Action {
    void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws IOException;
}
