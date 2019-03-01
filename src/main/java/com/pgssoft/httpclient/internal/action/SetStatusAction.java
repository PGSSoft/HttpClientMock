package com.pgssoft.httpclient.internal.action;

import com.pgssoft.httpclient.Action;
import com.pgssoft.httpclient.MockedServerResponse;

public final class SetStatusAction implements Action {

    private final int status;

    public SetStatusAction(int status) {
        this.status = status;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) {
        responseBuilder.setStatusCode(status);
    }
}
