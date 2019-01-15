package com.pgssoft.httpclient;

import com.pgssoft.httpclient.internal.UrlConditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlParsingTest {

    private static final int EMPTY_PORT_NUMBER = -1;

    @Test
    public void parseHost() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        // TODO: Should this assert be enabled? I don't think we should get a -1 when port is missing.   ~rskupnik
        //assertTrue(urlConditions.getPortConditions().get(0).matches(EMPTY_PORT_NUMBER));
        assertTrue(urlConditions.getReferenceConditions().matches(""));
    }

    @Test
    public void parseHostWithPort() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost:8080");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        assertTrue(urlConditions.getPortConditions().get(0).matches(8080));
    }

    @Test
    public void parseHostAndPath() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost/foo/bar");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        assertTrue(urlConditions.getPathConditions().get(0).matches("/foo/bar"));
    }

    @Test
    public void parseHostAndPathAndParameters() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost/foo/bar?a=1&b=2");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        assertTrue(urlConditions.getPathConditions().get(0).matches("/foo/bar"));
        assertTrue(urlConditions.getParameterConditions().get("a").get(0).matches("1"));
        assertTrue(urlConditions.getParameterConditions().get("b").get(0).matches("2"));
    }

    @Test
    public void parseHostReference() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost#abc");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        assertTrue(urlConditions.getReferenceConditions().matches("abc"));
    }

}


