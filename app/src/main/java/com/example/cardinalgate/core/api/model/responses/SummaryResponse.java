package com.example.cardinalgate.core.api.model.responses;

import com.google.gson.annotations.SerializedName;

public class SummaryResponse {
    @SerializedName("play_counts")
    public PlayCount[] playCounts;

    public static class PlayCount {
        @SerializedName("game")
        public String game;

        @SerializedName("count")
        public long count;
    }
}
