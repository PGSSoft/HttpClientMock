package com.pgssoft.action;

import com.pgssoft.HttpResponseProxy;

public final class SetBodyStringAction implements Action {

    private final String content;

    public SetBodyStringAction(String content) {
        this.content = content;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws Exception {
        responseBuilder.setBody(content);
    }
}
