package com.pgssoft.httpclient.internal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlParamsTest {

    @Test
    void shouldParseQueryString() {
        UrlParams urlParams = UrlParams.parse("a=1&b=2");
        List<ParameterValue> params = urlParams.getParams();
        assertEquals("a", params.get(0).getName());
        assertEquals("1", params.get(0).getValues().get(0));
        assertEquals("b", params.get(1).getName());
        assertEquals("2", params.get(1).getValues().get(0));
        assertTrue(urlParams.contains("a"));
        assertTrue(urlParams.contains("b"));
        assertFalse(urlParams.contains("c"));
    }

    @Test
    void shouldReturnEmptyListForNull() {
        assertEquals(0, UrlParams.parse(null).getParams().size());
    }

}
