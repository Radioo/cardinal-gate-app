package com.example.cardinalgate.core;

import com.example.cardinalgate.core.api.model.responses.SummaryResponse;

import java.util.HashMap;

public class UserDataManager {
    public static HashMap<String, Integer> profileIdMap = new HashMap<>();

    public static void setProfileIds(SummaryResponse.Profile[] profiles) {
        profileIdMap.clear();

        for (SummaryResponse.Profile profile : profiles) {
            profileIdMap.put(profile.game, profile.profileId);
        }
    }
}
