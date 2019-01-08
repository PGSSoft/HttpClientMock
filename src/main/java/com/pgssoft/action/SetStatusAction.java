package com.pgssoft.action;

import com.pgssoft.MockResponseBuilder;

public final class SetStatusAction implements Action {

    private final int status;

    public SetStatusAction(int status) {
        this.status = status;
    }

    @Override
    public void enrichResponse(MockResponseBuilder responseBuilder) {
        responseBuilder.setStatusCode(status);
    }
}
