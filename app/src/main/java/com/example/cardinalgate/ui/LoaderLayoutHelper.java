package com.example.cardinalgate.ui;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cardinalgate.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LoaderLayoutHelper {
    private ConstraintLayout loaderConstraint;
    private CircularProgressIndicator spinner;
    private TextView errorText;
    private Button retryButton;
    private ConstraintLayout contentConstraint;

    public LoaderLayoutHelper(View rootView) {
        loaderConstraint = rootView.findViewById(R.id.loaderConstraint);
        spinner = rootView.findViewById(R.id.spinner);
        errorText = rootView.findViewById(R.id.errorText);
        retryButton = rootView.findViewById(R.id.retryButton);
//        contentConstraint = rootView.findViewById(R.id.innerConstraint);
    }

    public void toggleLoader(boolean isLoading) {
        if (isLoading) {
            spinner.setVisibility(View.VISIBLE);
        }
        else {
            spinner.setVisibility(View.GONE);
        }
    }
}
