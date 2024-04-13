package com.example.cardinalgate.core.callback;

import com.example.cardinalgate.core.api.model.responses.GetCardsResponse;

public interface SetHostCardAction {
    boolean onSetHostCard(GetCardsResponse.Card card);
}
