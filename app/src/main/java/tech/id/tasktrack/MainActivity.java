package tech.id.tasktrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.model.Schedule;
import tech.id.tasktrack.model.ScheduleResponse;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_FINE_LOCATION = 1001;
    private static final int REQ_BACKGROUND_LOCATION = 1002;
    private static final int REQ_NOTIFICATION = 1003;
    ApiService api;
    SessionManager session;
    RecyclerView rvSchedule;
    ScheduleAdapter adapter;
    ProgressBar progressBar;
    ImageView ivLoadSchedule;

    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.pgbLoadSchedule);
        rvSchedule = findViewById(R.id.rvSchedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        ivLoadSchedule = findViewById(R.id.ivLoadSchedule);

        checkNotificationPermission();
        dbHelper = new DatabaseHelper(this);



        api = ApiClient.getClient().create(ApiService.class);
        session = new SessionManager(this);
        Pegawai p = dbHelper.getPegawai();

        ivLoadSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSchedule();
            }
        });

//        loadPegawai();

//        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadSchedule() {
        int pegawaiId = session.getPegawaiId();
        String token = session.getToken();
        ivLoadSchedule.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        api.getScheduleByPegawai(token, pegawaiId)
                .enqueue(new Callback<ScheduleResponse>() {
                    @Override
                    public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {

                            List<Schedule> schedules = response.body().data;

                            // SIMPAN KE SQLITE
                            dbHelper.insertSchedule(schedules);
                            adapter = new ScheduleAdapter(MainActivity.this, schedules);
                            rvSchedule.setAdapter(adapter);

                        } else {
                            // Jika API gagal, ambil dari database lokal
                            List<Schedule> localSchedules = dbHelper.getSchedulesByPegawai(pegawaiId);

                            if (!localSchedules.isEmpty()) {
//                                adapter.setData(localSchedules);
                                adapter = new ScheduleAdapter(MainActivity.this, localSchedules);
                                rvSchedule.setAdapter(adapter);

                                Toast.makeText(MainActivity.this, "Offline mode: data lokal ditampilkan", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Tidak ada data", Toast.LENGTH_SHORT).show();
                            }
                        }
                        ivLoadSchedule.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                        // Ambil dari SQLite saat offline
                        List<Schedule> localSchedules = dbHelper.getSchedulesByPegawai(pegawaiId);
                        rvSchedule.setAdapter(adapter);

                        if (!localSchedules.isEmpty()) {
//                            adapter.setData(localSchedules);
                            Toast.makeText(MainActivity.this, "Offline mode: data lokal ditampilkan", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        ivLoadSchedule.setVisibility(View.VISIBLE);

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
        } else {
            startWifiMonitorService();
        }
    }

    private void startWifiMonitorService() {
        Intent serviceIntent = new Intent(this, WifiMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Service WiFi berjalan di background ✅", Toast.LENGTH_LONG).show();
    }

    @Override
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

    private void loadPegawai() {
        String token = session.getToken();

        api.getPegawai(token).enqueue(new Callback<List<Pegawai>>() {
            @Override
            public void onResponse(Call<List<Pegawai>> call, Response<List<Pegawai>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Pegawai p : response.body()) {
                        sb.append(p.name).append(" (").append(p.email).append(")\n");
                    }
//                    tvResult.setText(sb.toString());
                } else {
//                    tvResult.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<List<Pegawai>> call, Throwable t) {
//                tvResult.setText("Error: " + t.getMessage());
            }
        });
    }

    private void logout() {
        String token = session.getToken();

        api.logout(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                session.clearSession();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}