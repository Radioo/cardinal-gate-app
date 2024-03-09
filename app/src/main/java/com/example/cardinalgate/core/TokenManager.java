package com.example.cardinalgate.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class TokenManager {
    @Nullable
    public static String token;
    private final Context context;

    TokenManager(Context context) {
        this.context = context;
    }

    public void loadToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cg_local_token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
    }

    public void saveToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cg_local_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();

        TokenManager.token = token;
    }
}
