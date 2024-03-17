package com.example.cardinalgate.ui.iidx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.calls.SetNotifyForIIDXRivalCall;
import com.example.cardinalgate.core.api.model.responses.IIDXGetRivalsResponse;
import com.example.cardinalgate.ui.UIHelper;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IIDXRivalsRecyclerViewAdapter extends RecyclerView.Adapter<IIDXRivalsRecyclerViewAdapter.ViewHolder> {
    private final List<IIDXGetRivalsResponse.IIDXRival> rivals;
    private final LayoutInflater inflater;
    private final APIInterface apiClient;
    private final Context context;

    IIDXRivalsRecyclerViewAdapter(Context context, List<IIDXGetRivalsResponse.IIDXRival> rivals) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.rivals = rivals;
        apiClient = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.iidx_rival_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IIDXGetRivalsResponse.IIDXRival rival = rivals.get(position);
        holder.djName.setText(String.format("DJ %s", rival.djName));
        holder.IIDXId.setText(formatIIDXId(rival.rivalId));
        holder.notifyCheckBox.setChecked(rival.notify == 1);

        holder.notifyCheckBox.setOnClickListener(buttonView -> {
            CheckBox checkBoxView = (CheckBox) buttonView;
            checkBoxView.setVisibility(View.GONE);
            holder.loader.setVisibility(View.VISIBLE);
            holder.deleteButton.setEnabled(false);
            boolean isChecked = holder.notifyCheckBox.isChecked();

            SetNotifyForIIDXRivalCall callBody = new SetNotifyForIIDXRivalCall();
            callBody.iidxRivalId = rival.iidxRivalId;
            callBody.notify = isChecked;

            Call<Void> call = apiClient.setNotifyForIIDXRival(callBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(context, "Notification settings updated", Toast.LENGTH_SHORT).show();
                    checkBoxView.setVisibility(View.VISIBLE);
                    holder.loader.setVisibility(View.GONE);
                    holder.deleteButton.setEnabled(true);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    UIHelper.handleAPIError(context, t);
                    checkBoxView.setChecked(!isChecked);
                    checkBoxView.setVisibility(View.VISIBLE);
                    holder.loader.setVisibility(View.GONE);
                    holder.deleteButton.setEnabled(true);
                }
            });
        });

        holder.deleteButton.setOnClickListener(buttonView -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Delete Rival");
            builder.setMessage("Are you sure you want to delete this rival?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                holder.deleteButton.setEnabled(false);
                holder.loader.setVisibility(View.VISIBLE);
                holder.notifyCheckBox.setVisibility(View.GONE);

                Call<Void> call = apiClient.removeIIDXRival(rival.iidxRivalId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        rivals.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, rivals.size());
                        Toast.makeText(context, "Rival removed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        UIHelper.handleAPIError(context, t);
                        buttonView.setEnabled(true);
                        holder.loader.setVisibility(View.GONE);
                        holder.notifyCheckBox.setVisibility(View.VISIBLE);
                    }
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return rivals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView djName;
        TextView IIDXId;
        ImageView swordsIcon;
        CheckBox notifyCheckBox;
        CircularProgressIndicator loader;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            djName = itemView.findViewById(R.id.rivalNameText);
            IIDXId = itemView.findViewById(R.id.rivalIIDXId);

            notifyCheckBox = itemView.findViewById(R.id.rivalNotificationButton);
            swordsIcon = itemView.findViewById(R.id.swordsIcon);
            loader = itemView.findViewById(R.id.rivalsProgressIndicator);
            deleteButton = itemView.findViewById(R.id.deleteRivalButton);

            int colorOnSurface = MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorOnSurface);

            swordsIcon.setColorFilter(colorOnSurface);
        }
    }

    IIDXGetRivalsResponse.IIDXRival getItem(int id) {
        return rivals.get(id);
    }

    private String formatIIDXId(int id) {
        String idString = String.valueOf(id);
        int length = idString.length();

        if(length < 8) {
            idString = String.join("", Collections.nCopies(8 - length, "0")) + idString;
        }

        return idString.substring(0, 4) + "-" + idString.substring(4);
    }
}
