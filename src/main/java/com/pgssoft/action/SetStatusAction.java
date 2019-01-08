package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

public final class SetStatusAction implements Action {

    private final int status;

    public SetStatusAction(int status) {
        this.status = status;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) {
        responseBuilder.setStatusCode(status);
    }
}
