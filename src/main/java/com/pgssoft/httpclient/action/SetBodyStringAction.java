package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.HttpResponseProxy;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class SetBodyStringAction implements Action {

    private final String content;

    public SetBodyStringAction(String content) {
        this.content = content;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws IOException {
        responseBuilder.setBody(content);
        responseBuilder.setBytes(ByteBuffer.wrap(content.getBytes()));
    }
}
