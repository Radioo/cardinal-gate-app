package com.example.cardinalgate.ui.iidx;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.calls.IIDXAddRivalCall;
import com.example.cardinalgate.core.api.model.responses.IIDXGetRivalsResponse;
import com.example.cardinalgate.core.enums.IIDXPlayStyle;
import com.example.cardinalgate.ui.UIHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IIDXRivalsFragment extends Fragment {
    public APIInterface apiClient;
    private Context context;
    private final ActivityResultLauncher requestPermissionLauncher;

    private CircularProgressIndicator rivalsProgressIndicator;
    private TextView spText;
    private ConstraintLayout spConstraint;
    private Button addSpRivalButton;
    private TextView dpText;
    private ConstraintLayout dpConstraint;
    private Button addDpRivalButton;

    public IIDXRivalsFragment() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                (result) -> {
                    if (result) {
                        Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiClient = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_i_i_d_x_rivals, container, false);

        rivalsProgressIndicator = view.findViewById(R.id.rivalsProgressIndicator);
        spText = view.findViewById(R.id.spText);
        spConstraint = view.findViewById(R.id.spConstraint);
        addSpRivalButton = view.findViewById(R.id.addSpRivalButton);
        dpText = view.findViewById(R.id.dpText);
        dpConstraint = view.findViewById(R.id.dpConstraint);
        addDpRivalButton = view.findViewById(R.id.addDpRivalButton);

        addSpRivalButton.setOnClickListener(l -> onAddRival(IIDXPlayStyle.SINGLE, inflater.inflate(R.layout.iidx_id_input, null)));

        addDpRivalButton.setOnClickListener(l -> onAddRival(IIDXPlayStyle.DOUBLE, inflater.inflate(R.layout.iidx_id_input, null)));

        loadRivals();

        if(!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        return view;
    }

    private void loadRivals() {
        setLoadingState(true);

        Call<IIDXGetRivalsResponse> call = apiClient.getIIDXRivals();
        call.enqueue(new Callback<IIDXGetRivalsResponse>() {
            @Override
            public void onResponse(@NonNull Call<IIDXGetRivalsResponse> call, @NonNull Response<IIDXGetRivalsResponse> response) {
                IIDXGetRivalsResponse rivalsResponse = response.body();
                populateRivals(rivalsResponse);
                setLoadingState(false);
            }

            @Override
            public void onFailure(@NonNull Call<IIDXGetRivalsResponse> call, @NonNull Throwable t) {
                UIHelper.handleAPIError(context, t);
                setLoadingState(false);
            }
        });
    }

    private void populateRivals(IIDXGetRivalsResponse response) {
        Context context = getContext();
        View view = getView();

        List<IIDXGetRivalsResponse.IIDXRival> spRivals = new ArrayList<>();
        List<IIDXGetRivalsResponse.IIDXRival> dpRivals = new ArrayList<>();

        for(IIDXGetRivalsResponse.IIDXRival rival : response.rivals) {
            IIDXPlayStyle playStyle = IIDXPlayStyle.fromValue(rival.playStyle);

            if(playStyle == IIDXPlayStyle.SINGLE) {
                spRivals.add(rival);
            }
            else {
                dpRivals.add(rival);
            }
        }

        RecyclerView spRivalsView = view.findViewById(R.id.iidxSpRecycler);
        spRivalsView.setLayoutManager(getLayoutManagerForRecyclerView());
        spRivalsView.setAdapter(getAdapterForRecyclerView(context, spRivals));

        RecyclerView dpRivalsView = view.findViewById(R.id.iidxDpRecycler);
        dpRivalsView.setLayoutManager(getLayoutManagerForRecyclerView());
        dpRivalsView.setAdapter(getAdapterForRecyclerView(context, dpRivals));

        if(spRivals.size() == 6) {
            addSpRivalButton.setEnabled(false);
        }

        if(dpRivals.size() == 6) {
            addDpRivalButton.setEnabled(false);
        }
    }

    private IIDXRivalsRecyclerViewAdapter getAdapterForRecyclerView(Context context, List<IIDXGetRivalsResponse.IIDXRival> rivals) {
        IIDXRivalsRecyclerViewAdapter adapter = new IIDXRivalsRecyclerViewAdapter(context, rivals);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                addSpRivalButton.setEnabled(itemCount != 6);
            }
        });

        return adapter;
    }

    private LinearLayoutManager getLayoutManagerForRecyclerView() {
        return new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
    }

    // This is bad
    private void setLoadingState(boolean loading) {
        if(loading) {
            spText.setVisibility(View.INVISIBLE);
            spConstraint.setVisibility(View.INVISIBLE);
            addSpRivalButton.setVisibility(View.INVISIBLE);
            dpText.setVisibility(View.INVISIBLE);
            dpConstraint.setVisibility(View.INVISIBLE);
            addDpRivalButton.setVisibility(View.INVISIBLE);

            rivalsProgressIndicator.setVisibility(View.VISIBLE);
        }
        else {
            spText.setVisibility(View.VISIBLE);
            spConstraint.setVisibility(View.VISIBLE);
            addSpRivalButton.setVisibility(View.VISIBLE);
            dpText.setVisibility(View.VISIBLE);
            dpConstraint.setVisibility(View.VISIBLE);
            addDpRivalButton.setVisibility(View.VISIBLE);

            rivalsProgressIndicator.setVisibility(View.GONE);
        }
    }

    private void onAddRival(IIDXPlayStyle playStyle, View idEditView) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Add Rival");
        builder.setMessage("Enter IIDX ID");

        TextInputEditText part1Edit = idEditView.findViewById(R.id.iidxIdPart1);
        TextView separator = idEditView.findViewById(R.id.separator);
        TextInputEditText part2Edit = idEditView.findViewById(R.id.iidxIdPart2);
        CircularProgressIndicator progressIndicator = idEditView.findViewById(R.id.rivalsProgressIndicator);
        Button cancelButton = idEditView.findViewById(R.id.cancelButton);
        Button addButton = idEditView.findViewById(R.id.addButton);

        Runnable setLoadingState = () -> {
            part1Edit.setVisibility(View.INVISIBLE);
            separator.setVisibility(View.INVISIBLE);
            part2Edit.setVisibility(View.INVISIBLE);
            progressIndicator.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.INVISIBLE);
        };

        Runnable removeLoadingState = () -> {
            part1Edit.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
            part2Edit.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        };

        part1Edit.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(4),
                (source, start, end, dest, dstart, dend) -> {
                    if(dend == 3) {
                        part2Edit.requestFocus();
                    }

                    return null;
                }
        });

        part2Edit.setOnKeyListener((v, keyCode, event) -> {
            Editable text = part2Edit.getText();
            if(text == null) {
                return false;
            }

            int textLength = text.toString().length();
            if(textLength == 0 && keyCode == 67) {
                part1Edit.requestFocus();
            }

            return false;
        });

        builder.setView(idEditView);
        AlertDialog dialog = builder.show();

        cancelButton.setOnClickListener(l2 -> dialog.dismiss());

        addButton.setOnClickListener(l3 -> {
            setLoadingState.run();

            IIDXAddRivalCall callBody = new IIDXAddRivalCall();

            callBody.rivalId = part1Edit.getText().toString() + part2Edit.getText().toString();
            callBody.playStyle = playStyle.getValue();

            Call<Void> call = apiClient.addIIDXRival(callBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(context, "Rival added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadRivals();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    UIHelper.handleAPIError(context, t);
                    removeLoadingState.run();
                }
            });
        });
    }
}