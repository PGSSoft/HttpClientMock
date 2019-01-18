package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.MockedServerResponse;

import java.io.IOException;

public final class ThrowExceptionAction implements Action {

    private final IOException exception;

    public ThrowExceptionAction(IOException exception) {
        this.exception = exception;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) throws IOException {
        throw exception;
    }
}
