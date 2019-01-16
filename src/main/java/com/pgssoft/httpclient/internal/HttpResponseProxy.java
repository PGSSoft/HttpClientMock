package com.pgssoft.httpclient.internal;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.*;

public class HttpResponseProxy<T> implements HttpResponse<T> {

    private final int statusCode;
    private final HttpHeaders headers;
    private final T body;
    private final ByteBuffer bytes;

    private HttpResponseProxy(int statusCode, HttpHeaders headers, T body, ByteBuffer bytes) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.bytes = bytes;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public HttpRequest request() {
        return null;
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return Optional.empty();
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return Optional.empty();
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public HttpClient.Version version() {
        return null;
    }

    public ByteBuffer getBytes() {
        return bytes;
    }

    public final static class Builder {

        private int statusCode;
        private Map<String, List<String>> headers = new HashMap<>();
        private Object body;
        private ByteBuffer bytes;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        public void addHeader(String key, String value) {
            headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        public ByteBuffer getBytes() {
            return bytes;
        }

        public void setBytes(ByteBuffer bytes) {
            this.bytes = bytes;
        }

        public <T> HttpResponseProxy<T> build() {
            return new HttpResponseProxy<T>(statusCode, HttpHeaders.of(headers, (a, b) -> true), (T) body, bytes);
        }
    }
}
