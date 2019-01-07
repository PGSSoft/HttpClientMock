package com.pgssoft.action;

import com.pgssoft.HttpResponseMock;
import com.pgssoft.MockResponseBuilder;

public interface Action {
    void enrichResponse(MockResponseBuilder responseBuilder) throws Exception;
}
