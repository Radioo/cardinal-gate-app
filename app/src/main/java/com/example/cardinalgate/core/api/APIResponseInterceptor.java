package com.example.cardinalgate.core.api;

import androidx.annotation.NonNull;

import com.example.cardinalgate.core.api.model.responses.BaseErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class APIResponseInterceptor implements Interceptor {
    private final Gson gson = new Gson();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int responseCode = response.code();

        if(responseCode != 200) {
            String responseBody = response.body().string();
            BaseErrorResponse errorResponse = gson.fromJson(responseBody, BaseErrorResponse.class);
            String errorText = errorResponse.error == null ? "Unknown error" : errorResponse.error;

            if(responseCode == 401) {
                throw new APIUnauthorizedException(errorText);
            }
            else {
                throw new APIException(errorText);
            }
        }

        return response;
    }
}
