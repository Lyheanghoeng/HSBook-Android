package com.hsbook.Retrofit;

public interface ServiceMangerCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
