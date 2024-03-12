package com.example.cardinalgate.core.api;

import androidx.annotation.NonNull;

import com.example.cardinalgate.core.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if(TokenManager.token == null) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .addHeader("CG-Token", TokenManager.token)
                .build();

        return chain.proceed(newRequest);
    }
}
