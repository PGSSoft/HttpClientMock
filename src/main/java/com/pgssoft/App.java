package com.pgssoft;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        HttpClient httpClient = new HttpClientMock();
        var req = HttpRequest.newBuilder(URI.create("https://www.google.com"))
                .POST(HttpRequest.BodyPublishers.ofString("123"))
                .build();
        var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println( res.body() );
    }
}
