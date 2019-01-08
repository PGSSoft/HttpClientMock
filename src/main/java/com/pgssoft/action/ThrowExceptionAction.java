package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

public final class ThrowExceptionAction implements Action {

    private final Exception exception;

    public ThrowExceptionAction(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws Exception {
        throw exception;
    }
}
