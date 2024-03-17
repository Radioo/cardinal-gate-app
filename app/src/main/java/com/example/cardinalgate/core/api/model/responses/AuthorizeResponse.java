package com.example.cardinalgate.core.api.model.responses;

import com.google.gson.annotations.SerializedName;

public class AuthorizeResponse {
    @SerializedName("token")
    public String token;

    @SerializedName("account_id")
    public int accountId;
}
