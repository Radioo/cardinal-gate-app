package com.example.cardinalgate.core.api.model.calls;

import com.google.gson.annotations.SerializedName;

public class SetNotifyForIIDXRivalCall {
    @SerializedName("iidx_rival_id")
    public int iidxRivalId;

    @SerializedName("notify")
    public boolean notify;
}
