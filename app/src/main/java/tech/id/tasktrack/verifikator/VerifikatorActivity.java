package tech.id.tasktrack.verifikator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import tech.id.tasktrack.R;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.main.MainActivity;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.verifikator.model.PegawaiSimple;

public class VerifikatorActivity extends AppCompatActivity {

    TextView tvTotalEmployes, tvTotalTaskCompleted;
    int pegawaiId;
    DatabaseHelper db;
    SessionManager session;
    TextView tvNamaPegawai;
    ImageView ivFotoProfil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verifikator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalEmployes = findViewById(R.id.tvTotalEmployes);
        tvTotalTaskCompleted = findViewById(R.id.tvTotalTaskCompleted);
        tvNamaPegawai = findViewById(R.id.tvNamaPegawai);
        ivFotoProfil = findViewById(R.id.ivFotoProfil);

        db = new DatabaseHelper(this);

        String tanggal = "2025-11-21";

        List<PegawaiSimple> pegawaiList = db.getDistinctPegawaiByTanggal(tanggal);

        RecyclerView rv = findViewById(R.id.rvListEmpoyeeToday);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PegawaiListAdapter(this, pegawaiList));

        int totalPegawai = db.getTotalPegawaiByTanggal(tanggal);

        int totalTask = db.getTotalTaskByTanggal(tanggal);

        tvTotalEmployes.setText(String.valueOf(totalPegawai));
        tvTotalTaskCompleted.setText(String.valueOf(totalTask));

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();

        Pegawai p = db.getPegawai(pegawaiId);

        tvNamaPegawai.setText(p.name);
        Glide.with(VerifikatorActivity.this)
                .load("http://172.15.1.239:8000/storage/"+p.foto)
                .circleCrop()
                .placeholder(R.drawable.username)  // tampilkan gambar default saat loading
                .error(R.drawable.username)        // jika gagal load
                .into(ivFotoProfil);

    }
}