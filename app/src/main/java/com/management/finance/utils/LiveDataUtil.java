package com.management.finance.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataUtil {

    public static <T> T getValue(final LiveData<T> liveData, long timeOut) throws InterruptedException {
        final T[] data = (T[]) new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);

        // Pastikan observeForever dipanggil di Main Thread
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                observeLiveData(liveData, data, latch);
            });
        } else {
            observeLiveData(liveData, data, latch);
        }

        latch.await(timeOut, TimeUnit.SECONDS);
        return data[0];
    }

    private static <T> void observeLiveData(final LiveData<T> liveData, final T[] data, final CountDownLatch latch) {
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
    }
}