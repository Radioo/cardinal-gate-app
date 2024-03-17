package com.example.cardinalgate.core.api.model.responses;

import com.google.gson.annotations.SerializedName;

public class SummaryResponse {
    @SerializedName("play_counts")
    public PlayCount[] playCounts;

    @SerializedName("profiles")
    public Profile[] profiles;

    public static class PlayCount {
        @SerializedName("game")
        public String game;

        @SerializedName("count")
        public long count;
    }

    public static class Profile {
        @SerializedName("game")
        public String game;

        @SerializedName("profile_id")
        public int profileId;
    }
}
