package com.pgssoft;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MockResponseBuilder {

    private int statusCode;
    private Map<String, List<String>> headers = Map.of();

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

    public <T> HttpResponseMock<T> build() {
        return new HttpResponseMock<>(statusCode, HttpHeaders.of(headers, (a, b) -> true));
    }
}
