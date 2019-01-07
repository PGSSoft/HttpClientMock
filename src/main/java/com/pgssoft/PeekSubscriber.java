package com.pgssoft;

import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

public class PeekSubscriber implements Flow.Subscriber<ByteBuffer> {

    private Flow.Subscription subscription;
    private ByteBuffer content;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(ByteBuffer item) {
        content = item;
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {

    }

    public ByteBuffer content() {
        return content;
    }
}
