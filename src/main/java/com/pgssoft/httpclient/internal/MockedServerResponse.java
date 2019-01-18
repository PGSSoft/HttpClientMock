package com.pgssoft.httpclient.internal;

import java.net.http.HttpHeaders;
import java.nio.ByteBuffer;
import java.util.*;

public class MockedServerResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final ByteBuffer bytes;

    private MockedServerResponse(int statusCode, Map<String, List<String>> headers, ByteBuffer bytes) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.bytes = bytes;
    }

    public int statusCode() {
        return statusCode;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public ByteBuffer getBytes() {
        return bytes;
    }

    public HttpHeaders httpHeaders() {
        return HttpHeaders.of(headers(),(a,b)->true);
    }

    public final static class Builder {

        private int statusCode;
        private Map<String, List<String>> headers = new HashMap<>();
        private ByteBuffer bytes;


        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public void addHeader(String key, String value) {
            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        public void setBytes(ByteBuffer bytes) {
            this.bytes = bytes;
        }

        public MockedServerResponse build() {
            return new MockedServerResponse(statusCode,headers,  bytes);
        }
    }
}
