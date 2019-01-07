package com.pgssoft.action;

import com.pgssoft.MockResponseBuilder;

public final class SetBodyStringAction implements Action {

    private final String content;

    public SetBodyStringAction(String content) {
        this.content = content;
    }

    @Override
    public void enrichResponse(MockResponseBuilder responseBuilder) throws Exception {
        responseBuilder.setBody(content);
    }
}
