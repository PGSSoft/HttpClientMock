package com.pgssoft.httpclient;

@FunctionalInterface
interface ThrowingRunnable {
    void run() throws Exception;
}
