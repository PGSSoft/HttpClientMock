package com.pgssoft.httpclient.internal.debug;

import com.pgssoft.httpclient.internal.rule.Rule;

import java.net.http.HttpRequest;
import java.util.List;

public class Debugger {

    public void debug(List<Rule> rules, HttpRequest request) {
        logRequest(request);
        logRules(rules, request);
    }

    private void logRules(List<Rule> rules, HttpRequest request) {
        if (rules.size() == 0) {
            System.out.println("No rules were defined.");
        }
        for (int i = 0; i < rules.size(); i++) {
            System.out.println("Rule " + (i + 1) + ":");
            System.out.println("\tMATCHES\t\tEXPECTED");
            rules.get(i).debug(request, this);
        }
        System.out.println();
        System.out.println("----------------");

    }

    private void logRequest(HttpRequest request) {
        System.out.println("Request: " + request.method() + " " + request.uri());
    }

    public void message(boolean matches, String expected) {
        String debugMessage = String.format("\t%s\t\t%s", matches, expected);
        System.out.println(debugMessage);
    }
}
