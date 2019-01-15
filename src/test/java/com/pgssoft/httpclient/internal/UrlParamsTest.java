package com.pgssoft.httpclient.internal;

import com.pgssoft.httpclient.internal.KeyValuePair;
import com.pgssoft.httpclient.internal.UrlParams;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlParamsTest {

    @Test
    public void shouldParseQueryString() {
        List<KeyValuePair> params = UrlParams.parse("a=1&b=2");
        assertEquals("a", params.get(0).getKey());
        assertEquals("1", params.get(0).getValue());
        assertEquals("b", params.get(1).getKey());
        assertEquals("2", params.get(1).getValue());
    }

    @Test
    public void shouldReturnEmptyListForNull() {
        assertEquals(0, UrlParams.parse(null).size());
    }

}
