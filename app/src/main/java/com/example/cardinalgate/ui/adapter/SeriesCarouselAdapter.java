package com.example.cardinalgate.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.model.responses.SummaryResponse;

import java.util.ArrayList;
import java.util.Locale;

public class SeriesCarouselAdapter extends RecyclerView.Adapter<SeriesCarouselAdapter.ViewHolder> {
    private Context context;
    ArrayList<SummaryResponse.PlayCount> playCounts;

    public SeriesCarouselAdapter(Context context, ArrayList<SummaryResponse.PlayCount> playCounts) {
        this.context = context;
        this.playCounts = playCounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.summary_carousel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SummaryResponse.PlayCount playCount = playCounts.get(position);

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), playCount.game.getHomeImage(), null);

        holder.seriesImage.setImageDrawable(drawable);
        holder.playCount.setText(formatPlayCount(playCount.count));
    }

    @Override
    public int getItemCount() {
        return playCounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView seriesImage;
        private TextView playCount;

        public ViewHolder(@NonNull View parent) {
            super(parent);

            seriesImage = parent.findViewById(R.id.seriesImage);
            playCount = parent.findViewById(R.id.songsPlayedLabel);
        }
    }

    private String formatPlayCount(long count) {
        return String.format(Locale.getDefault(), "%,d", count);
    }
}
