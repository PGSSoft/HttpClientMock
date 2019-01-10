package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

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
