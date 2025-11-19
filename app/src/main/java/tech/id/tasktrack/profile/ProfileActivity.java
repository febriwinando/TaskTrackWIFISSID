package tech.id.tasktrack.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import tech.id.tasktrack.R;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.main.MainActivity;
import tech.id.tasktrack.model.Pegawai;

public class ProfileActivity extends AppCompatActivity {

    SessionManager session;
    DatabaseHelper dbHelper;

    ImageView ivBackProfile, ivFotoProfil;
    int pegawaiId;
    TextView tvNameProfile, tvNIK, tvEmployeID, tvEmail, tvPhoneNumber;
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

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();
        Pegawai p = dbHelper.getPegawai(pegawaiId);


        tvNameProfile.setText(p.name);
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

    }
}