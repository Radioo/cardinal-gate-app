package com.example.cardinalgate.core.api.model.responses;

import com.example.cardinalgate.core.enums.CardType;
import com.google.gson.annotations.SerializedName;

public class GetCardsResponse {
    @SerializedName("cards")
    public Card[] cards;

    public static class Card {
        @SerializedName("id")
        public String id;
        @SerializedName("display_id")
        public String displayId;
        @SerializedName("type")
        public CardType type;

        @SerializedName("added")
        public String added;
    }
}
