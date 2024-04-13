package com.example.cardinalgate.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ContextMenu;
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
import com.example.cardinalgate.ui.adapter.SeriesCarouselAdapter;
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
    private ImageView homeMascot;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        noPlaysLabel = mainView.findViewById(R.id.noPlaysLabel);
        gameSummaryConstraint = mainView.findViewById(R.id.gameSummaryConstraint);
        clockIcon = mainView.findViewById(R.id.clockIcon);
        estimatedPlayTime = mainView.findViewById(R.id.estimatedPlayTime);
        homeMascot = mainView.findViewById(R.id.homeMascot);

        setColors();
        loadSummary();
        addMascotOnClickListener();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.mascot_menu, menu);
        menu.setHeaderTitle("Mascot Menu");
    }

    @Override
    public boolean onContextItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.menuGm) {
            sayGm();
        }

        return super.onContextItemSelected(item);
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

    @SuppressLint("ClickableViewAccessibility")
    private void addMascotOnClickListener() {
        registerForContextMenu(homeMascot);
        homeMascot.setOnTouchListener((v, event) -> {
            if(event.getAction() == android.view.MotionEvent.ACTION_UP) {
                homeMascot.showContextMenu(event.getX(), event.getY());
            }

            return true;
        });
    }

    private void sayGm() {
        homeMascot.setEnabled(false);

        apiInterface.sayGm().enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                makeSnackBar("gm");
                homeMascot.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleAPIError(t);
                homeMascot.setEnabled(true);
            }
        });
    }
}