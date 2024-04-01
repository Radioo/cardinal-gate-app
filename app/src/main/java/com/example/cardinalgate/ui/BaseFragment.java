package com.example.cardinalgate.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.cardinalgate.LoginActivity;
import com.example.cardinalgate.R;
import com.example.cardinalgate.core.TokenManager;
import com.example.cardinalgate.core.api.APIUnauthorizedException;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    protected View mainView;

    private CircularProgressIndicator spinner;
    private ImageView mascotImage;
    private TextView errorText;
    private Button retryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);

        ConstraintLayout outerConstraint = rootView.findViewById(R.id.baseConstraint);

        int viewId = getLayoutId();
        mainView = inflater.inflate(viewId, container, false);
        outerConstraint.addView(mainView);

        ConstraintSet set = new ConstraintSet();
        set.clone(outerConstraint);
        set.connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        set.connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        set.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.applyTo(outerConstraint);

        spinner = rootView.findViewById(R.id.spinner);
        mascotImage = rootView.findViewById(R.id.mascotImage);
        errorText = rootView.findViewById(R.id.errorText);
        retryButton = rootView.findViewById(R.id.retryButton);

        return rootView;
    }

    protected void showLoader() {
        mainView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
    }

    protected void hideLoader(boolean restoreMainView) {
        if(restoreMainView) {
            mainView.setVisibility(View.VISIBLE);
        }

        spinner.setVisibility(View.GONE);
    }

    protected void setRetryButtonOnClickListener(Runnable action) {
        retryButton.setVisibility(View.VISIBLE);

        retryButton.setOnClickListener((view) -> {
            mascotImage.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);

            clearRetryButtonOnClickListener();
            action.run();
        });
    }

    protected void clearRetryButtonOnClickListener() {
        retryButton.setVisibility(View.GONE);
        retryButton.setOnClickListener(null);
    }

    protected void handleAPIError(Throwable t) {
        hideLoader(false);
        mascotImage.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.VISIBLE);

        Context context = requireContext();
        String message = Objects.requireNonNull(t.getMessage());

        if(t instanceof APIUnauthorizedException) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            requireActivity().finish();

            TokenManager.deleteToken(context);

            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        else {
            Snackbar.make(mainView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    abstract protected int getLayoutId();
}
