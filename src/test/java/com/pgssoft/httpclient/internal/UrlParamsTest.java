package com.pgssoft.httpclient.internal;

import com.pgssoft.httpclient.internal.KeyValuePair;
import com.pgssoft.httpclient.internal.UrlParams;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlParamsTest {

    @Test
    public void shouldParseQueryString() {
        List<ParameterValue> params = UrlParams.parse("a=1&b=2").getParams();
        assertEquals("a", params.get(0).getName());
        assertEquals("1", params.get(0).getValues().get(0));
        assertEquals("b", params.get(1).getName());
        assertEquals("2", params.get(1).getValues().get(0));
    }

    @Test
    public void shouldReturnEmptyListForNull() {
        assertEquals(0, UrlParams.parse(null).getParams().size());
    }

}
