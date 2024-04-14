package com.example.cardinalgate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.model.responses.GetCardsResponse;
import com.example.cardinalgate.core.callback.SetHostCardAction;
import com.example.cardinalgate.ui.UIHelper;

import java.util.ArrayList;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private final ArrayList<GetCardsResponse.Card> cards;
    private final Context context;
    private final LayoutInflater inflater;
    private final boolean supportsHCEF;
    private final SetHostCardAction onSetHostCard;

    public CardRecyclerAdapter(Context context, ArrayList<GetCardsResponse.Card> cards, boolean supportsHCEF, SetHostCardAction onSetHostCard) {
        this.context = context;
        this.cards = cards;
        this.inflater = LayoutInflater.from(context);
        this.supportsHCEF = supportsHCEF;
        this.onSetHostCard = onSetHostCard;
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
        holder.addedAt.setText(card.displayId);

        holder.setHostCard.setOnClickListener(v -> {
            onSetHostCard.onSetHostCard(card);
        });

        if(!supportsHCEF) {
            holder.setHostCard.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public GetCardsResponse.Card getItem(int position) {
        return cards.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardIcon;
        private final TextView cardId;
        private final TextView addedAt;
        private final Button setHostCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardIcon = itemView.findViewById(R.id.cardIcon);
            cardId = itemView.findViewById(R.id.cardId);
            addedAt = itemView.findViewById(R.id.displayId);
            setHostCard = itemView.findViewById(R.id.setHostCard);

            cardIcon.setColorFilter(UIHelper.getColorOnSurface(itemView));
        }
    }
}
