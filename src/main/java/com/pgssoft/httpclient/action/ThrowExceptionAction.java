package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.HttpResponseProxy;

import java.io.IOException;

public final class ThrowExceptionAction implements Action {

    private final IOException exception;

    public ThrowExceptionAction(IOException exception) {
        this.exception = exception;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws IOException {
        throw exception;
    }
}
