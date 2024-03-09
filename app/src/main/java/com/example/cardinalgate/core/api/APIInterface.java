package com.example.cardinalgate.core.api;

import com.example.cardinalgate.core.api.model.calls.AuthorizeCall;
import com.example.cardinalgate.core.api.model.responses.AuthorizeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIInterface {
    @POST("/authorize")
    Call<AuthorizeResponse> authorize(@Body AuthorizeCall call);
}
