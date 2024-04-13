package com.example.cardinalgate.ui.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.responses.GetCardsResponse;
import com.example.cardinalgate.core.callback.CardUnlinkAction;
import com.example.cardinalgate.ui.UIHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private final ArrayList<GetCardsResponse.Card> cards;
    private final Context context;
    private final LayoutInflater inflater;
    private final APIInterface apiClient;
    private final CardUnlinkAction onCardUnlinked;

    public CardRecyclerAdapter(Context context, ArrayList<GetCardsResponse.Card> cards, CardUnlinkAction onCardUnlinked) {
        this.context = context;
        this.cards = cards;
        this.inflater = LayoutInflater.from(context);
        this.apiClient = APIClient.getClient().create(APIInterface.class);
        this.onCardUnlinked = onCardUnlinked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.holder_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int positionInHolder = holder.getAdapterPosition();

        GetCardsResponse.Card card = cards.get(positionInHolder);
        holder.cardIcon.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.credit_card, null));
        holder.cardId.setText(card.id);
        holder.addedAt.setText(String.format(context.getString(R.string.card_added_at), card.added));

        holder.unlinkButton.setOnClickListener(v -> {
            onCardUnlinked.onCardUnlink(card);
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardIcon;
        private final TextView cardId;
        private final TextView addedAt;
        private final Button unlinkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardIcon = itemView.findViewById(R.id.cardIcon);
            cardId = itemView.findViewById(R.id.cardId);
            addedAt = itemView.findViewById(R.id.addedAt);
            unlinkButton = itemView.findViewById(R.id.unlinkButton);

            cardIcon.setColorFilter(UIHelper.getColorOnSurface(itemView));
        }
    }
}
