package com.example.cardinalgate.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.UserDataManager;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.responses.SummaryResponse;
import com.example.cardinalgate.databinding.FragmentHomeBinding;
import com.example.cardinalgate.ui.UIHelper;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private APIInterface apiClient;
    private TableLayout totalPlayCountsTable;
    private ProgressBar homeProgressBar;
    private TextView estimatedTotalPlayTime;
    private onSummaryRequestResponse listener;
    private Button gmButton;

    public interface onSummaryRequestResponse {
        void onSummaryResponse(SummaryResponse response);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onSummaryRequestResponse) {
            listener = (onSummaryRequestResponse) context;
        } else {
            throw new ClassCastException(context + " must implement HomeFragment.onSummaryRequestResponse");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiClient = APIClient.getClient().create(APIInterface.class);

        totalPlayCountsTable = root.findViewById(R.id.totalPlayCountsTable);
        homeProgressBar = root.findViewById(R.id.homeProgressBar);
        estimatedTotalPlayTime = root.findViewById(R.id.estimatedTotalPlayTimeLabel);
        gmButton = root.findViewById(R.id.gmButton);

        loadSummary();

        RecyclerView carouselRecyclerView = root.findViewById(R.id.carousel_recycler_view);

        ImageAdapter imageAdapter = new ImageAdapter(getContext());
        carouselRecyclerView.setAdapter(imageAdapter);

        SnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(carouselRecyclerView);

        gmButton.setOnClickListener(l -> {
            Call<Void> call = apiClient.sayGm();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(getContext(), "gm", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    UIHelper.handleAPIError(getContext(), t);
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadSummary() {
        Call<SummaryResponse> call = apiClient.getSummary();
        call.enqueue(new Callback<SummaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<SummaryResponse> call, @NonNull Response<SummaryResponse> response) {
                SummaryResponse summaryResponse = response.body();

                if(summaryResponse == null) {
                    Toast.makeText(getContext(), "Failed to load summary", Toast.LENGTH_SHORT).show();
                    hideLoader();
                    return;
                }

                if(listener != null) {
                    listener.onSummaryResponse(summaryResponse);
                }

                parseSummaryData(summaryResponse);
                UserDataManager.setProfileIds(summaryResponse.profiles);
                hideLoader();
            }

            @Override
            public void onFailure(@NonNull Call<SummaryResponse> call, @NonNull Throwable t) {
                UIHelper.handleAPIError(getContext(), t);
                hideLoader();
            }
        });
    }

    private void hideLoader() {
        homeProgressBar.setVisibility(View.GONE);
    }

    private void parseSummaryData(SummaryResponse response) {
        Context context = requireContext();
        long totalPlays = 0L;

        for(SummaryResponse.PlayCount playCount : response.playCounts) {
            totalPlays += playCount.count;

            String game = playCount.game;
            String gameKey = "game_" + game;
            @SuppressLint("DiscouragedApi")
            String gameTranslated = getString(getResources().getIdentifier(gameKey, "string", requireContext().getPackageName()));

            TableRow row = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView gameView = new TextView(context);
            gameView.setText(gameTranslated);

            TextView countView = new TextView(context);
            countView.setText(String.format(Locale.getDefault(), "%,d", playCount.count));

            row.addView(gameView);
            row.addView(countView);

            totalPlayCountsTable.addView(row);
        }

        float totalHours = totalPlays / 30.0f;
        DecimalFormat format = new DecimalFormat("0.##");

        String labelBase = getString(R.string.label_estimated_total_play_time);
        String labelFormatted = String.format(labelBase, format.format(totalHours));
        estimatedTotalPlayTime.setText(labelFormatted);
    }
}