package com.github.clans.daviart.util;

import rx.Observer;
import timber.log.Timber;

public class ObserverImpl<T> implements Observer<T> {

    @Override
    public void onCompleted() {
        Timber.d("Loading completed");
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "An error occurred");
    }

    @Override
    public void onNext(T t) {
        Timber.d("onNext()");
    }
}
