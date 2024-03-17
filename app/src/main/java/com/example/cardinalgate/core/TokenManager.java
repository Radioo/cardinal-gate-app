package com.example.cardinalgate.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

public class TokenManager {
    @Nullable
    public static String token;

    public static void loadToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cg_local_token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        Log.d("TokenManager", "Token loaded: " + token);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cg_local_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();

        TokenManager.token = token;
        Log.d("TokenManager", "Token saved: " + token);
    }

    public static void deleteToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cg_local_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        TokenManager.token = null;
        Log.d("TokenManager", "Token deleted");
    }
}
