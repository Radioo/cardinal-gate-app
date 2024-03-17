package com.example.cardinalgate.core.api.model.responses;

import com.google.gson.annotations.SerializedName;

public class IIDXGetRivalsResponse {
    @SerializedName("rivals")
    public IIDXRival[] rivals;

    public static class IIDXRival {
        @SerializedName("iidx_rival_id")
        public int iidxRivalId;

        @SerializedName("rival_id")
        public int rivalId;

        @SerializedName("play_style")
        public int playStyle;

        @SerializedName("notify")
        public int notify;

        @SerializedName("dj_name")
        public String djName;
    }
}
