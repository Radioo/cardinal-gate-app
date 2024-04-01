package com.example.cardinalgate.core;

import com.example.cardinalgate.core.api.model.responses.SummaryResponse;
import com.example.cardinalgate.core.enums.Series;

import java.util.HashMap;

public class UserDataManager {
    public static HashMap<Series, Integer> profileIdMap = new HashMap<>();

    public static void setProfileIds(SummaryResponse.Profile[] profiles) {
        profileIdMap.clear();

        for (SummaryResponse.Profile profile : profiles) {
            profileIdMap.put(profile.game, profile.profileId);
        }
    }

    public static boolean hasSeriesProfile(Series series) {
        return profileIdMap.containsKey(series);
    }
}
