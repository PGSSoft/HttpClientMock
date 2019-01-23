package com.pgssoft.httpclient;

import java.nio.ByteBuffer;
import java.util.*;

public class MockedServerResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final ByteBuffer bodyBytes;

    private MockedServerResponse(int statusCode, Map<String, List<String>> headers, ByteBuffer bodyBytes) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.bodyBytes = bodyBytes;
    }

    public int statusCode() {
        return statusCode;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public ByteBuffer getBodyBytes() {
        return bodyBytes;
    }


    public final static class Builder {

        private int statusCode;
        private Map<String, List<String>> headers = new HashMap<>();
        private ByteBuffer bodyBytes = ByteBuffer.wrap(new byte[]{});

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public void addHeader(String key, String value) {
            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        public void setBodyBytes(ByteBuffer bodyBytes) {
            this.bodyBytes = bodyBytes;
        }

        public MockedServerResponse build() {
            return new MockedServerResponse(statusCode, headers, bodyBytes);
        }
    }
}
