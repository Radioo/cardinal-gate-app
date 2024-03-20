package com.example.cardinalgate.core.api;

import com.example.cardinalgate.core.api.model.calls.AuthorizeCall;
import com.example.cardinalgate.core.api.model.calls.IIDXAddRivalCall;
import com.example.cardinalgate.core.api.model.calls.SetNotifyForIIDXRivalCall;
import com.example.cardinalgate.core.api.model.responses.AuthorizeResponse;
import com.example.cardinalgate.core.api.model.responses.IIDXGetRivalsResponse;
import com.example.cardinalgate.core.api.model.responses.SummaryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {
    @POST("/authorize")
    Call<AuthorizeResponse> authorize(@Body AuthorizeCall call);

    @GET("/summary")
    Call<SummaryResponse> getSummary();

    @GET("/say_gm")
    Call<Void> sayGm();

    @GET("iidx/rival")
    Call<IIDXGetRivalsResponse> getIIDXRivals();

    @POST("iidx/rival")
    Call<Void> addIIDXRival(@Body IIDXAddRivalCall call);

    @DELETE("iidx/rival/{id}")
    Call<Void> removeIIDXRival(@Path("id") int id);

    @PUT("iidx/rival/notify")
    Call<Void> setNotifyForIIDXRival(@Body SetNotifyForIIDXRivalCall call);
}
