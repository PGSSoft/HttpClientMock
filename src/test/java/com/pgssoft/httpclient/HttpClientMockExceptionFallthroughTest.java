package com.pgssoft.httpclient;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpClientMockExceptionFallthroughTest {

    @Test
    void runtimeExceptionShouldFallThroughUnchanged() {

        var httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("localhost")
                .withPath("/login")
                .doReturn(401, "You are not authorized.");

        var client = new LoginClient(httpClientMock);

        assertThrows(ClientSideException.class, () -> client.login());
    }

    static class ClientSideException extends RuntimeException {
        public ClientSideException(String message) {
            super(message);
        }
    }

    static class LoginClient {

        private final HttpClient httpClient;

        public LoginClient(HttpClient httpClient) {
            this.httpClient = requireNonNull(httpClient, "httpClient");
        }

        public String login() {
            var uri = URI.create("http://localhost/login");
            var request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
            try {
                var response = httpClient.send(request, this::handleBody);
                return response.body();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private HttpResponse.BodySubscriber<String> handleBody(HttpResponse.ResponseInfo responseInfo) {
            var statusCode = responseInfo.statusCode();
            var readBody = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            if (statusCode == 200 || statusCode == 204) {
                return readBody;
            }
            return HttpResponse.BodySubscribers.mapping(readBody, message -> {
                throw new ClientSideException(message);
            });
        }
    }
}
