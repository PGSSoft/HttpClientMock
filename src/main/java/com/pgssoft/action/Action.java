package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

public interface Action {
    void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws Exception;
}
