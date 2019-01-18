package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.internal.HttpResponseProxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class SetBodyStringAction implements Action {

    private final String content;
    private final Charset charset;

    public SetBodyStringAction(String content) {
        this(content, StandardCharsets.UTF_8);
    }

    public SetBodyStringAction(String content, Charset charset) {
        this.content = content;
        this.charset = charset;
    }

    @Override
    public void enrichResponse(HttpResponseProxy.Builder responseBuilder) throws IOException {
        responseBuilder.setBody(content);
        responseBuilder.setBytes(charset.encode(content));
    }
}
