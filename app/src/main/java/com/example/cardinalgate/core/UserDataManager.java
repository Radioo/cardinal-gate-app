package com.example.cardinalgate.core;

import android.view.MenuItem;
import android.view.View;

import com.example.cardinalgate.core.api.model.responses.SummaryResponse;
import com.example.cardinalgate.core.enums.Series;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class UserDataManager {
    public static HashMap<Series, Integer> profileIdMap = new HashMap<>();

    private static NavigationView navView;

    public static void setProfileIds(SummaryResponse.Profile[] profiles) {
        profileIdMap.clear();

        for (SummaryResponse.Profile profile : profiles) {
            profileIdMap.put(profile.game, profile.profileId);

            Integer navMenuId = profile.game.getNavMenu();
            if(navMenuId == null) {
                continue;
            }

            MenuItem item = navView.getMenu().findItem(navMenuId);
            item.setVisible(true);
        }
    }

    public static void setNavView(NavigationView navView) {
        UserDataManager.navView = navView;
    }

    public static boolean hasSeriesProfile(Series series) {
        return profileIdMap.containsKey(series);
    }

    public static boolean shouldShowIIDXMenu() {
        return hasSeriesProfile(Series.IIDX);
    }
}
