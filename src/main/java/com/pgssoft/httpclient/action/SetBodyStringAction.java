package com.pgssoft.httpclient.action;

import com.pgssoft.httpclient.Action;
import com.pgssoft.httpclient.MockedServerResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class SetBodyStringAction implements Action {

    private final String content;
    private final Charset charset;

    public SetBodyStringAction(String content, Charset charset) {
        this.content = content;
        this.charset = charset;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) {
        responseBuilder.setBodyBytes(charset.encode(content));
    }
}
