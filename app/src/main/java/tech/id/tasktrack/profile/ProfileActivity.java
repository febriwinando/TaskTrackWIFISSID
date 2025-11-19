package tech.id.tasktrack.profile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tech.id.tasktrack.R;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.model.Pegawai;

public class ProfileActivity extends AppCompatActivity {

    SessionManager session;
    DatabaseHelper dbHelper;

    int pegawaiId;
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

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();

        Pegawai p = dbHelper.getPegawai(pegawaiId);
    }
}