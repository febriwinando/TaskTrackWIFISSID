package tech.id.tasktrack.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.main.MainActivity;
import tech.id.tasktrack.R;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.model.LoginRequest;
import tech.id.tasktrack.model.LoginResponse;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.model.PegawaiResponse;
import tech.id.tasktrack.model.Schedule;
import tech.id.tasktrack.model.ScheduleResponse;
import tech.id.tasktrack.verifikator.VerifikatorActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int REQ_FINE_LOCATION = 1001;
    private static final int REQ_BACKGROUND_LOCATION = 1002;
    private static final int REQ_NOTIFICATION = 1003;
    ProgressBar progressBar;
    TextInputEditText tietEmployeeID, tietPassword;
    Button btnLogin;
    ApiService api;
    SessionManager session;

    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        session = new SessionManager(this);
        if (session.getToken() != null) {
            Log.d("LevelUser", session.getPegawaiLevel());
            if (Objects.equals(session.getPegawaiLevel(), "officer")){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return;
            }else{
                startActivity(new Intent(LoginActivity.this, VerifikatorActivity.class));
                finish();
                return;
            }
        }
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

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        checkNotificationPermission();


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
                    session.savePegawaiId(response.body().pegawai.id);
                    session.savePegawaiLevel(response.body().pegawai.level);

                    Pegawai pegawai = response.body().pegawai;
                    dbHelper.insertPegawai(pegawai);

                    if (pegawai.level != "officer"){
                        getListScheduleEmployees();
                    }else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                } else {
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

    private void getListScheduleEmployees() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        String token = session.getToken();
        int pegawaiId = session.getPegawaiId();

        api.listScheduleEmployees(token, pegawaiId, "2025-11-21").enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleResponse> call, @NonNull Response<ScheduleResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (response.isSuccessful() && response.body() != null) {



                    List<Schedule> schedules = response.body().data;
                    dbHelper.insertListScheduleEmployees(schedules);

                    startActivity(new Intent(LoginActivity.this, VerifikatorActivity.class));
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ScheduleResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIFICATION);
            } else {
                checkFineLocationPermission();
            }
        } else {
            checkFineLocationPermission();
        }
    }

    private void checkFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            new AlertDialog.Builder(this)
                    .setTitle("Izin Diperlukan")
                    .setMessage("Aplikasi memerlukan akses lokasi untuk membaca SSID WiFi.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_FINE_LOCATION))
                    .setNegativeButton("Batal", null)
                    .show();
        } else {
            checkBackgroundLocationPermission();
        }
    }

    private void checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQ_BACKGROUND_LOCATION);
            } else {
                checkLocationEnabled();
            }
        } else {
            checkLocationEnabled();
        }
    }

    private void checkLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("Aktifkan Lokasi")
                    .setMessage("Aplikasi memerlukan lokasi aktif untuk membaca SSID WiFi.")
                    .setPositiveButton("Buka Pengaturan", (dialog, which) ->
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Batal", null)
                    .show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBackgroundLocationPermission();
            } else {
                Toast.makeText(this, "Izin lokasi ditolak ❌", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQ_BACKGROUND_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationEnabled();
            } else {
                Toast.makeText(this, "Izin background lokasi ditolak ❌", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQ_NOTIFICATION) {
            checkFineLocationPermission();
        }
    }

}