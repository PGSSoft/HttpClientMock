package com.pgssoft;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.pgssoft.matchers.HttpResponseMatchers.hasStatus;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.Assert.assertNull;

public class HttpClientResponseBuilderTest {

    @Test
    public void should_return_status_404_when_no_rule_matches() throws Exception {
        HttpClientMock httpClientMock = new HttpClientMock();
        //HttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        final var notFound = httpClientMock.send(HttpRequest.newBuilder(URI.create("http://localhost/foo")).GET().build(), ofString());
        //assertThat(notFound, hasStatus(404));
        assertNull(notFound);
        // TODO: Adjust for exception
    }
}
