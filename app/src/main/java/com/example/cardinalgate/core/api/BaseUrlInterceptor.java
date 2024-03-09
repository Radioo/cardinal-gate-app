package com.example.cardinalgate.core.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {
    private static final String API_PATH = "api2";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();

        List<String> segments = new ArrayList<>(originalHttpUrl.pathSegments());
        segments.add(0, API_PATH);

        HttpUrl newUrl = new HttpUrl.Builder()
                .scheme(originalHttpUrl.scheme())
                .host(originalHttpUrl.host())
                .port(originalHttpUrl.port())
                .addPathSegments(String.join("/", segments))
                .build();

        Request newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build();

        return chain.proceed(newRequest);
    }
}
