package com.pgssoft.httpclient;

import com.pgssoft.httpclient.internal.UrlConditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlConditionsTest {

    private static final int EMPTY_PORT_NUMBER = -1;

    @Test
    void parseHost() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost");
        assertTrue(urlConditions.getSchemaCondition().matches("http"));
        assertTrue(urlConditions.getHostCondition().matches("localhost"));
        assertTrue(urlConditions.getPortCondition().matches(EMPTY_PORT_NUMBER));
        assertTrue(urlConditions.getReferenceCondition().matches(""));
    }


    @Test
    void parseHostWithPort() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost:8080");
        assertTrue(urlConditions.getHostCondition().matches("localhost"));
        assertTrue(urlConditions.getPortCondition().matches(8080));
    }

    @Test
    void parseHostAndPath() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost/foo/bar");
        assertTrue(urlConditions.getHostCondition().matches("localhost"));
        assertTrue(urlConditions.getPathCondition().matches("/foo/bar"));
    }

    @Test
    void parseHostAndPathAndParameters() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost/foo/bar?a=1&b=2");
        assertTrue(urlConditions.getHostCondition().matches("localhost"));
        assertTrue(urlConditions.getPathCondition().matches("/foo/bar"));
        assertTrue(urlConditions.getParameterConditions().matches("a=1&b=2"));
        assertTrue(urlConditions.getParameterConditions().matches("b=2&a=1"));
    }

    @Test
    void parseHostReference() {
        UrlConditions urlConditions = UrlConditions.parse("http://localhost#abc");
        assertTrue(urlConditions.getHostCondition().matches("localhost"));
        assertTrue(urlConditions.getReferenceCondition().matches("abc"));
    }

}


