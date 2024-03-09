package com.example.cardinalgate.ui;

import android.content.Context;
import android.widget.Toast;

public class UIHelper {
    public static void handleAPIError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
