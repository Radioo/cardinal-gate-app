package com.example.cardinalgate.core.api.model.responses;

import com.example.cardinalgate.core.enums.Series;
import com.google.gson.annotations.SerializedName;

public class SummaryResponse {
    @SerializedName("play_counts")
    public PlayCount[] playCounts;

    @SerializedName("profiles")
    public Profile[] profiles;

    public static class PlayCount {
        @SerializedName("game")
        public Series game;

        @SerializedName("count")
        public long count;
    }

    public static class Profile {
        @SerializedName("game")
        public Series game;

        @SerializedName("profile_id")
        public int profileId;
    }
}
