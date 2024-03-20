package com.example.cardinalgate.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.cardinalgate.LoginActivity;
import com.example.cardinalgate.core.TokenManager;
import com.example.cardinalgate.core.api.APIUnauthorizedException;

public class UIHelper {
    public static void handleAPIError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

        if(t instanceof APIUnauthorizedException) {
            Activity activity = (Activity) context;
            activity.finish();

            TokenManager.deleteToken(context);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
