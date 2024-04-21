package com.example.cardinalgate.core.api.model.responses;

import com.google.gson.annotations.SerializedName;

public class SDVXGetStatsResponse {
    @SerializedName("lamp_data")
    public LampDatum[] lampData;
    @SerializedName("grade_data")
    public GradeDatum[] gradeData;

    public static class LampDatum {
        @SerializedName("lamp")
        public int lamp;

        @SerializedName("levels")
        public Integer[] levels;
    }

    public static class GradeDatum {
        @SerializedName("grade")
        public int grade;

        @SerializedName("levels")
        public Integer[] levels;
    }
}
