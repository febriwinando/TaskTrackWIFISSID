package tech.id.tasktrack;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.model.Schedule;
import tech.id.tasktrack.model.ScheduleResponse;

public class ScheduleActivity extends AppCompatActivity {

    ShimmerFrameLayout shimmerSchedule;
    RecyclerView rvSchedule;

    int currentMonth;
    int currentYear;
    DatabaseHelper db;
    ScheduleAdapter adapter;

    ImageView btnPrev, btnNext, ivBack, ivSyncSchedule;
    TextView txtBulan;
    SessionManager session;

    ProgressBar pgbLoadSchedule;
    int pegawaiId;
    ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();

        shimmerSchedule = findViewById(R.id.shimmerSchedule);
        rvSchedule = findViewById(R.id.rvSchedule);
        rvSchedule.setHasFixedSize(true);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        txtBulan = findViewById(R.id.txtBulan);
        ivBack = findViewById(R.id.ivBack);
        pgbLoadSchedule = findViewById(R.id.pgbLoadSchedule);
        ivSyncSchedule = findViewById(R.id.ivSyncSchedule);

        db = new DatabaseHelper(this);

        Calendar c = Calendar.getInstance();
        currentMonth = c.get(Calendar.MONTH) + 1;
        currentYear = c.get(Calendar.YEAR);
        api = ApiClient.getClient().create(ApiService.class);

        loadCalendar(currentMonth, currentYear); // pertama kali tampil

        btnNext.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            loadCalendar(currentMonth, currentYear);
        });

        btnPrev.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            loadCalendar(currentMonth, currentYear);
        });
//        showRecyclerGrid();
        shimmerSchedule.stopShimmer();
        shimmerSchedule.hideShimmer();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSyncSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSchedule();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();   // atau aksi lain
            }
        });


    }



    private void loadCalendar(int bulan, int tahun) {
        txtBulan.setText(getNamaBulan(currentMonth) + " " + currentYear);
        List<Schedule> list = db.getSchedulesByPegawai(pegawaiId);

        adapter = new ScheduleAdapter(
                ScheduleActivity.this,
                list,
                currentYear,
                currentMonth
        );

        rvSchedule.setLayoutManager(new GridLayoutManager(this, 4));
        rvSchedule.setAdapter(adapter);
    }


    private String getNamaBulan(int month) {
        String[] bulan = {
                "Januari","Februari","Maret","April","Mei","Juni",
                "Juli","Agustus","September","Oktober","November","Desember"
        };
        return bulan[month - 1];
    }

    private void loadSchedule() {
        String token = session.getToken();
        ivSyncSchedule.setVisibility(View.INVISIBLE);
        pgbLoadSchedule.setVisibility(View.VISIBLE);

        api.getScheduleByMonth(token, pegawaiId, currentMonth, currentYear)
                .enqueue(new Callback<ScheduleResponse>() {
                    @Override
                    public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                        pgbLoadSchedule.setVisibility(View.INVISIBLE);

                        if (response.isSuccessful() && response.body() != null) {

                            List<Schedule> schedules = response.body().data;
                            // SIMPAN KE SQLITE (khusus bulan + tahun ini)
                            db.insertScheduleByMonth(schedules, currentMonth, currentYear);
                            loadCalendar(currentMonth, currentYear);

                        } else {
                            loadCalendar(currentMonth, currentYear);
                        }

                        ivSyncSchedule.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                        pgbLoadSchedule.setVisibility(View.INVISIBLE);
                        loadCalendar(currentMonth, currentYear);
                        ivSyncSchedule.setVisibility(View.VISIBLE);

                    }
                });
    }


}