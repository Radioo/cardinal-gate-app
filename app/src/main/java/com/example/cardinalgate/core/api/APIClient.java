package com.example.cardinalgate.core.api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new BaseUrlInterceptor())
                .addInterceptor(new TokenInterceptor())
                .addNetworkInterceptor(new APIResponseInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(APIConfig.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
