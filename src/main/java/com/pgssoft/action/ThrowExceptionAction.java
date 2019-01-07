package com.pgssoft.action;

import com.pgssoft.MockResponseBuilder;

public final class ThrowExceptionAction implements Action {

    private final Exception exception;

    public ThrowExceptionAction(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void enrichResponse(MockResponseBuilder responseBuilder) throws Exception {
        throw exception;
    }
}
