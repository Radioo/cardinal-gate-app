package com.example.cardinalgate.core.callback;

import com.example.cardinalgate.core.api.model.responses.GetCardsResponse;

public interface CardUnlinkAction {
    void onCardUnlink(GetCardsResponse.Card card);
}
