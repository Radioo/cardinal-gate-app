package com.example.cardinalgate.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.UserDataManager;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.responses.SummaryResponse;
import com.example.cardinalgate.ui.BaseFragment;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.color.MaterialColors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;

public class HomeFragment extends BaseFragment {
    private APIInterface apiInterface;
    private TextView noPlaysLabel;
    private ConstraintLayout gameSummaryConstraint;
    private ImageView clockIcon;
    private TextView estimatedPlayTime;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        noPlaysLabel = mainView.findViewById(R.id.noPlaysLabel);
        gameSummaryConstraint = mainView.findViewById(R.id.gameSummaryConstraint);
        clockIcon = mainView.findViewById(R.id.clockIcon);
        estimatedPlayTime = mainView.findViewById(R.id.estimatedPlayTime);

        setColors();
        loadSummary();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    private void loadSummary() {
        showLoader();

        apiInterface.getSummary().enqueue(new retrofit2.Callback<SummaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<SummaryResponse> call, @NonNull retrofit2.Response<SummaryResponse> response) {
                SummaryResponse summary = response.body();

                assert summary != null;
                UserDataManager.setProfileIds(summary.profiles);
                if(summary.playCounts.length > 0) {
                    noPlaysLabel.setVisibility(View.GONE);
                    gameSummaryConstraint.setVisibility(View.VISIBLE);
                    setPlayCounts(summary);
                    setPlayTime(summary);
                }
                else {
                    noPlaysLabel.setVisibility(View.VISIBLE);
                    gameSummaryConstraint.setVisibility(View.GONE);
                }

                hideLoader(true);
            }

            @Override
            public void onFailure(@NonNull Call<SummaryResponse> call, @NonNull Throwable t) {
                handleAPIError(t);
                setRetryButtonOnClickListener(() -> loadSummary());
            }
        });
    }

    private void setPlayCounts(SummaryResponse response) {
        RecyclerView recyclerView = mainView.findViewById(R.id.seriesCarouselRecycler);
        ArrayList<SummaryResponse.PlayCount> playCounts = new ArrayList<>(Arrays.asList(response.playCounts));

        recyclerView.setAdapter(new SeriesCarouselAdapter(requireContext(), playCounts));
        recyclerView.setLayoutManager(new CarouselLayoutManager());

        SnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void setPlayTime(SummaryResponse response) {
        long totalPlays = Arrays.stream(response.playCounts)
                .mapToLong(playCount -> playCount.count)
                .reduce(0, Long::sum);

        Duration duration = Duration.ofMinutes(totalPlays * 2);
        long hours = duration.toHours();

        estimatedPlayTime.setText(String.format(Locale.getDefault(), "%,d hours", hours));
    }

    private void setColors() {
        int colorOnSurface = MaterialColors.getColor(mainView, com.google.android.material.R.attr.colorOnSurface);
        clockIcon.setColorFilter(colorOnSurface);
    }
}