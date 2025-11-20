package tech.id.tasktrack.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.R;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.login.LoginActivity;
import tech.id.tasktrack.main.MainActivity;
import tech.id.tasktrack.model.Pegawai;

public class ProfileActivity extends AppCompatActivity {

    SessionManager session;
    DatabaseHelper dbHelper;
    ApiService api;
    ImageView ivBackProfile, ivFotoProfil;
    int pegawaiId;
    TextView tvNameProfile, tvNIK, tvEmployeID, tvEmail, tvPhoneNumber;
    RelativeLayout rlLogoutProfil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBackProfile = findViewById(R.id.ivBackProfile);
        tvNameProfile = findViewById(R.id.tvNameProfile);
        ivFotoProfil = findViewById(R.id.ivFotoProfil);
        tvNIK = findViewById(R.id.tvNIK);
        tvEmployeID = findViewById(R.id.tvEmployeID);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        rlLogoutProfil = findViewById(R.id.rlLogoutProfil);

        api = ApiClient.getClient().create(ApiService.class);
        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();
        Pegawai p = dbHelper.getPegawai(pegawaiId);


        tvNameProfile.setText(p.name.toUpperCase());

        Glide.with(ProfileActivity.this)
                .load("http://172.15.1.239:8000/storage/"+p.foto)
                .circleCrop()
                .placeholder(R.drawable.username)  // tampilkan gambar default saat loading
                .error(R.drawable.username)        // jika gagal load
                .into(ivFotoProfil);

        tvNIK.setText(p.nik);
        tvEmployeID.setText(p.employee_id);
        tvEmail.setText(p.email);
        tvPhoneNumber.setText(p.nomor_wa);

        ivBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();   // atau aksi lain
            }
        });

        rlLogoutProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }


    public void logout() {
        String token = session.getToken();

        api.logout(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                session.clearSession();
                DatabaseHelper db = new DatabaseHelper(ProfileActivity.this);
                db.clearAllTables();   // atau db.resetDatabase();

                session.clearSession();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}