package com.example.cardinalgate.ui.home;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cardinalgate.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    ArrayList<String> arrayList;
    OnItemClickListener onItemClickListener;

    public ImageAdapter(Context context) {
        this.context = context;
        arrayList = new ArrayList<>();
        arrayList.add("cab_iidx");
        arrayList.add("cab_sdvx");
        arrayList.add("cab_popn");
        arrayList.add("cab_kbm");
        arrayList.add("cab_qma");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String drawable_name = arrayList.get(position);
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), context.getResources().getIdentifier(drawable_name, "drawable", context.getPackageName()), null);

        holder.imageView.setImageDrawable(drawable);
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(holder.imageView, arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_item_image);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, String path);
    }
}