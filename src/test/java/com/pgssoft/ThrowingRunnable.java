package com.pgssoft;

@FunctionalInterface
interface ThrowingRunnable {
    void run() throws Exception;
}
