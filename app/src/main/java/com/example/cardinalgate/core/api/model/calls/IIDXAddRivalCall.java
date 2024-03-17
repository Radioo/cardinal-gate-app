package com.example.cardinalgate.core.api.model.calls;

import com.google.gson.annotations.SerializedName;

public class IIDXAddRivalCall {
    @SerializedName("play_style")
    public int playStyle;

    @SerializedName("rival_id")
    public String rivalId;
}
