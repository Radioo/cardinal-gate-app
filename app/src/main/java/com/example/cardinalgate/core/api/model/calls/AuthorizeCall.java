package com.example.cardinalgate.core.api.model.calls;

import com.google.gson.annotations.SerializedName;

public class AuthorizeCall {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("device_name")
    public String deviceName;
}
