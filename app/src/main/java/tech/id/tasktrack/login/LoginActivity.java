package tech.id.tasktrack.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.MainActivity;
import tech.id.tasktrack.R;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.model.LoginRequest;
import tech.id.tasktrack.model.LoginResponse;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextInputEditText tietEmployeeID, tietPassword;
    Button btnLogin;
    ApiService api;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        tietEmployeeID = findViewById(R.id.tietEmployeeID);
        tietPassword = findViewById(R.id.tietPassword);
        btnLogin = findViewById(R.id.btnLogin);

        api = ApiClient.getClient().create(ApiService.class);
        session = new SessionManager(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        progressBar.setVisibility(android.view.View.VISIBLE);

        LoginRequest req = new LoginRequest(
                Objects.requireNonNull(tietEmployeeID.getText()).toString(),
                Objects.requireNonNull(tietPassword.getText()).toString()
        );

        api.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                progressBar.setVisibility(android.view.View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    session.saveToken("Bearer " + response.body().token);
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                    Log.d("Error Login", String.valueOf(response.body().message));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.d("Error Login", String.valueOf(response.isSuccessful()));
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}