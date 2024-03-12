package com.example.cardinalgate.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIException;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.responses.SummaryResponse;
import com.example.cardinalgate.databinding.FragmentHomeBinding;
import com.example.cardinalgate.ui.UIHelper;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private APIInterface apiClient;
    private TableLayout totalPlayCountsTable;
    private ProgressBar homeProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiClient = APIClient.getClient().create(APIInterface.class);

        totalPlayCountsTable = root.findViewById(R.id.totalPlayCountsTable);
        homeProgressBar = root.findViewById(R.id.homeProgressBar);

        loadSummary();

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

                parseSummaryData(summaryResponse);
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

        for(SummaryResponse.PlayCount playCount : response.playCounts) {
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
            countView.setText(String.valueOf(playCount.count));

            row.addView(gameView);
            row.addView(countView);

            totalPlayCountsTable.addView(row);
        }
    }
}