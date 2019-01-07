package com.pgssoft;

import com.pgssoft.condition.BodyCondition;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.SubmissionPublisher;

import static org.hamcrest.Matchers.containsString;

public final class HttpClientMock extends HttpClient {


    @Override
    public Optional<CookieHandler> cookieHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
        return null;
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
        return null;
    }

    @Override
    public SSLParameters sslParameters() {
        return null;
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return Optional.empty();
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public Optional<Executor> executor() {
        return Optional.empty();
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        final HttpResponse.ResponseInfo responseInfo = new HttpResponse.ResponseInfo() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.of(Map.of(), (a,b) -> true);
            }

            @Override
            public Version version() {
                return Version.HTTP_1_1;
            }
        };

        try {

            var bodyCondition = new BodyCondition(containsString("123"));
            bodyCondition.matches(request);

            var subscriber = responseBodyHandler.apply(responseInfo);

            var publisher = new SubmissionPublisher<List<ByteBuffer>>();
            publisher.subscribe(subscriber);
            publisher.submit(List.of(ByteBuffer.wrap(new byte[] {'a', 'l', 'a'})));
            publisher.close();

            return new HttpResponseMock(subscriber.getBody().toCompletableFuture().get());
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return null;
    }
}
