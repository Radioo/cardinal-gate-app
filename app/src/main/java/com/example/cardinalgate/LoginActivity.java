package com.example.cardinalgate;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cardinalgate.core.TokenManager;
import com.example.cardinalgate.core.api.APIClient;
import com.example.cardinalgate.core.api.APIConfig;
import com.example.cardinalgate.core.api.APIInterface;
import com.example.cardinalgate.core.api.model.calls.AuthorizeCall;
import com.example.cardinalgate.core.api.model.responses.AuthorizeResponse;
import com.example.cardinalgate.ui.UIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private APIInterface apiClient;
    private Button loginButton;
    private LottieAnimationView loginLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfTokenExists();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiClient = APIClient.getClient().create(APIInterface.class);

        loginButton = findViewById(R.id.loginButton);
        loginLoader = findViewById(R.id.loginLoader);

        loginButton.setOnClickListener(this::onLoginButtonClick);
    }

    protected void onLoginButtonClick(View view) {
        loginButton.setEnabled(false);
        loginLoader.setVisibility(View.VISIBLE);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if(userDeviceName == null) {
            Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        }

        AuthorizeCall callData = new AuthorizeCall();
        callData.username = username;
        callData.password = password;
        callData.deviceName = userDeviceName;

        Call<AuthorizeResponse> call = apiClient.authorize(callData);
        call.enqueue(new Callback<AuthorizeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorizeResponse> call, @NonNull Response<AuthorizeResponse> response) {
                AuthorizeResponse authorizeResponse = response.body();

                if (authorizeResponse == null || authorizeResponse.token == null || authorizeResponse.token.length() != APIConfig.TOKEN_LENGTH) {
                    Toast.makeText(LoginActivity.this, "Invalid token received", Toast.LENGTH_SHORT).show();
                }
                else {
                    TokenManager.saveToken(LoginActivity.this, authorizeResponse.token);
                    switchToMainActivity();
                }

                enableLoginAction();
            }

            @Override
            public void onFailure(@NonNull Call<AuthorizeResponse> call, @NonNull Throwable t) {
                UIHelper.handleAPIError(LoginActivity.this, t);

                enableLoginAction();
            }
        });
    }

    private void checkIfTokenExists() {
        TokenManager.loadToken(this);
        if (TokenManager.token != null) {
            switchToMainActivity();
        }
    }

    private void switchToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void enableLoginAction() {
        loginButton.setEnabled(true);
        loginLoader.setVisibility(View.INVISIBLE);
    }
}