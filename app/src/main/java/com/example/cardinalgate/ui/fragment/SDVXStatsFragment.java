package com.example.cardinalgate.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.View;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.responses.SDVXGetStatsResponse;
import com.example.cardinalgate.ui.UIHelper;
import com.github.AAChartModel.AAChartCore.AAChartCreator.AAChartModel;
import com.github.AAChartModel.AAChartCore.AAChartCreator.AAChartView;
import com.github.AAChartModel.AAChartCore.AAChartCreator.AASeriesElement;
import com.github.AAChartModel.AAChartCore.AAChartEnum.AAChartAnimationType;
import com.github.AAChartModel.AAChartCore.AAChartEnum.AAChartStackingType;
import com.github.AAChartModel.AAChartCore.AAChartEnum.AAChartType;
import com.github.AAChartModel.AAChartCore.AAOptionsModel.AAOptions;
import com.github.AAChartModel.AAChartCore.AAOptionsModel.AAStyle;
import com.github.AAChartModel.AAChartCore.AAOptionsModel.AATooltip;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SDVXStatsFragment extends BaseFragment {
    private static final String[] lamps = new String[]{"NO PLAY", "PLAYED", "CLEAR", "EXCESSIVE CLEAR", "UC", "PUC"};
    private static final String[] grades = new String[]{
            "S",
            "AAA+",
            "AAA",
            "AA+",
            "AA",
            "A+",
            "A",
            "B",
            "C",
            "D",
            "NO GRADE",
    };
    private AAChartView lampChart;
    private AAChartView gradeChart;
    private APIInterface apiInterface;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        lampChart = view.findViewById(R.id.lampChart);
        gradeChart = view.findViewById(R.id.gradeChart);

        getStats();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_d_v_x_stats;
    }

    private String[] getLevelCategories() {
        int maxLevel = 20;
        String[] categories = new String[maxLevel];
        for (int i = 0; i < maxLevel; i++) {
            categories[i] = "LV " + (i + 1);
        }

        return categories;
    }

    private Object[] getRandomLevelData() {
        int maxLevel = 20;
        Object[] data = new Integer[maxLevel];
        for (int i = 0; i < maxLevel; i++) {
            data[i] = (int) Math.floor(Math.random() * 100);
        }

        return data;
    }

    private AASeriesElement[] getRandomLamps() {
        String[] lamps = new String[]{"PUC", "UC", "EXCESSIVE CLEAR", "CLEAR", "PLAYED", "NO PLAY"};

        AASeriesElement[] elements = new AASeriesElement[lamps.length];
        for (int i = 0; i < lamps.length; i++) {
            elements[i] = new AASeriesElement()
                    .name(lamps[i])
                    .data(getRandomLevelData());
        }

        return elements;
    }

    private void getStats() {
        showLoader();

        apiInterface.getStats().enqueue(new Callback<SDVXGetStatsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SDVXGetStatsResponse> call, @NonNull Response<SDVXGetStatsResponse> response) {
                SDVXGetStatsResponse stats = response.body();
                assert stats != null;
                parseStats(stats);
            }

            @Override
            public void onFailure(@NonNull Call<SDVXGetStatsResponse> call, @NonNull Throwable t) {
                handleAPIError(t);
                setRetryButtonOnClickListener(() -> getStats());
            }
        });
    }

    private void parseStats(SDVXGetStatsResponse stats) {
        AASeriesElement[] lampElements = new AASeriesElement[stats.lampData.length];
        AASeriesElement[] gradeElements = new AASeriesElement[stats.gradeData.length];

        for(int i = 0; i < stats.lampData.length; i++) {
            SDVXGetStatsResponse.LampDatum datum = stats.lampData[i];

            lampElements[i] = new AASeriesElement()
                    .name(lamps[datum.lamp])
                    .data(datum.levels);
        }

        for(int i = 0; i < stats.gradeData.length; i++) {
            SDVXGetStatsResponse.GradeDatum datum = stats.gradeData[i];

            gradeElements[i] = new AASeriesElement()
                    .name(grades[datum.grade])
                    .data(datum.levels);
        }

        createChart(lampElements, lampChart, new String[]{"#717171", "#144513", "#448f44", "#5a3188", "#8c1446", "#9f960f"});
        createChart(gradeElements, gradeChart, new String[]{
                "#e5d31e",
                "#cebe1b",
                "#b7a918",
                "#a09415",
                "#897f12",
                "#736a0f",
                "#5c540c",
                "#453f09",
                "#2e2a06",
                "#171503",
                "#000000",
        });
        hideLoader(true);
    }

    private void createChart(AASeriesElement[] data, AAChartView chartView, String[] colors) {
        String colorOnSurface = UIHelper.getColorHex(UIHelper.getColorOnSurface(chartView));
        String colorSurface = UIHelper.getColorHex(UIHelper.getColorSurface(chartView));

        AATooltip tooltip = new AATooltip()
                .enabled(true)
                .shared(true)
                .backgroundColor(colorSurface)
                .borderColor(colorOnSurface)
                .style(new AAStyle()
                        .color(colorOnSurface));

        AAChartModel chartModel = new AAChartModel()
                .chartType(AAChartType.Bar)
                .backgroundColor(colorSurface)
                .categories(getLevelCategories())
                .legendEnabled(false)
                .yAxisGridLineWidth(0)
                .yAxisLabelsEnabled(false)
                .colorsTheme(colors)
                .animationType(AAChartAnimationType.EaseOutCubic)
                .animationDuration(1200)
                .stacking(AAChartStackingType.Percent)
                .series(data);

        AAOptions options = chartModel.aa_toAAOptions();
        options.tooltip(tooltip);

        chartView.aa_drawChartWithChartOptions(options);
    }
}