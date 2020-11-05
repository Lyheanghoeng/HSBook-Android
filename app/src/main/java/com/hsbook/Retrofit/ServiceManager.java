package com.hsbook.Retrofit;

import androidx.annotation.NonNull;

import com.hsbook.api.ApiUrl;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceManager {

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(new ApiUrl().getBASE_API())
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create());

//    private static Retrofit.Builder oneGetOuilder =
//            new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(httpClient)
//                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <T> T createService(Class<T> serviceClass) {

//        if (isOneGet) {
//            return oneGetOuilder.build().create(serviceClass);
//        }
        return retrofit.create(serviceClass);
    }

    public static <T> void enqueueWithRetry(Call<T> call, final ServiceMangerCallback<Response<T>> callback) {

        call.enqueue(new CallbackWithRetry<T>(call) {

            @Override
            void onFailedAfterRetry(Throwable t) {
                callback.onError(t.getMessage());
            }

            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                callback.onSuccess(response);
            }
        });
    }

    public static void enqueueDownload(Call<ResponseBody> call, final ServiceMangerCallback<Response<ResponseBody>> callback) {

        call.enqueue((Callback<ResponseBody>) new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
