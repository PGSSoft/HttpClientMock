package com.pgssoft.httpclient;

import java.util.Arrays;
import java.util.List;

public class Lists {
    public static <T> List<T> of(T ... args) {
        return Arrays.asList(args);
    }
}
