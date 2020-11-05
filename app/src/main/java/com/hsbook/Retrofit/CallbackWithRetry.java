package com.hsbook.Retrofit;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class CallbackWithRetry<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 3;
    private final Call<T> call;
    private int retryCount = 0;

    public CallbackWithRetry(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (retryCount++ < TOTAL_RETRIES) {
            Log.d("onFailure", "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry();
        } else {
            onFailedAfterRetry(t);
        }
    }

    private void retry() {
        call.clone().enqueue(this);
    }

    abstract void onFailedAfterRetry(Throwable t);

}