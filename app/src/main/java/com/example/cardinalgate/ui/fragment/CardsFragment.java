package com.example.cardinalgate.ui.fragment;

import static android.content.Context.NFC_SERVICE;

import android.graphics.Path;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cardinalgate.R;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.calls.AddCardCall;
import com.example.cardinalgate.core.api.model.responses.GetCardsResponse;
import com.example.cardinalgate.ui.adapter.CardRecyclerAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardsFragment extends BaseFragment {
    private APIInterface apiClient;
    private NfcManager nfcManager;

    private SwipeRefreshLayout cardSwipeRefreshLayout;
    private TextView noCardsFoundText;
    private RecyclerView cardsRecycler;
    private ExtendedFloatingActionButton addCardButton;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiClient = APIClient.getClient().create(APIInterface.class);
        nfcManager = (NfcManager) requireContext().getSystemService(NFC_SERVICE);

        cardSwipeRefreshLayout = mainView.findViewById(R.id.cardSwipeRefreshLayout);
        noCardsFoundText = mainView.findViewById(R.id.noCardsFoundText);
        cardsRecycler = mainView.findViewById(R.id.cardsRecycler);
        addCardButton = mainView.findViewById(R.id.addCardButton);

        cardSwipeRefreshLayout.setOnRefreshListener(this::getCards);
        addCardButton.setOnClickListener(v -> showAddCardDialog());

        getCards();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cards;
    }

    private void showAddCardDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.alert_add_card, null);

        TextInputLayout cardIdInput = dialogView.findViewById(R.id.cardIdInput);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button addButton = dialogView.findViewById(R.id.addButton);

        NfcAdapter adapter = nfcManager.getDefaultAdapter();
        if(adapter != null) {
            // Read NFC tag
            adapter.enableReaderMode(requireActivity(), tag -> {
                byte[] bytes = tag.getId();
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X", b));
                }

                cardIdInput.getEditText().setText(sb);
                cardIdInput.getEditText().setSelection(cardIdInput.getEditText().getText().length());
            }, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.show();

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            if(adapter != null) {
                adapter.disableReaderMode(requireActivity());
            }
        });

        addButton.setOnClickListener(v -> {
            cardIdInput.setEnabled(false);
            addButton.setEnabled(false);
            cancelButton.setEnabled(false);

            AddCardCall callBody = new AddCardCall();
            callBody.cardId = cardIdInput.getEditText().getText().toString();

            apiClient.addCard(callBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    dialog.dismiss();
                    if(adapter != null) {
                        adapter.disableReaderMode(requireActivity());
                    }

                    makeSnackBar("Card added successfully");
                    getCards();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    handleAPIError(t);

                    cardIdInput.setEnabled(true);
                    addButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }
            });
        });
    }

    private void getCards() {
        showLoader();

        apiClient.getCards().enqueue(new Callback<GetCardsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetCardsResponse> call, @NonNull Response<GetCardsResponse> response) {
                GetCardsResponse responseBody = response.body();

                assert responseBody != null;
                noCardsFoundText.setVisibility(responseBody.cards.length == 0 ? View.VISIBLE : View.GONE);

                parseCards(responseBody.cards);

                cardSwipeRefreshLayout.setRefreshing(false);
                hideLoader(true);
            }

            @Override
            public void onFailure(@NonNull Call<GetCardsResponse> call, @NonNull Throwable t) {
                cardSwipeRefreshLayout.setRefreshing(false);
                handleAPIError(t);
            }
        });
    }

    private void onCardUnlink(GetCardsResponse.Card card) {
        showLoader();

        apiClient.removeCard(card.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                makeSnackBar("Card unlinked successfully");
                getCards();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                hideLoader(true);
                makeSnackBar(t.getMessage());
            }
        });
    }

    private void parseCards(GetCardsResponse.Card[] cards) {
        ArrayList<GetCardsResponse.Card> cardList = new ArrayList<>(Arrays.asList(cards));

        cardsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        cardsRecycler.setAdapter(new CardRecyclerAdapter(requireContext(), cardList, this::onCardUnlink));
    }
}