package tech.id.tasktrack;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.model.Schedule;

public class ScheduleActivity extends AppCompatActivity {

    ShimmerFrameLayout shimmerSchedule;
    RecyclerView rvSchedule;

    int currentMonth;
    int currentYear;
    DatabaseHelper db;
    ScheduleAdapter adapter;

    ImageView btnPrev, btnNext;
    TextView txtBulan;
    SessionManager session;

    int pegawaiId;
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

        db = new DatabaseHelper(this);

        Calendar c = Calendar.getInstance();
        currentMonth = c.get(Calendar.MONTH) + 1;
        currentYear = c.get(Calendar.YEAR);

        loadCalendar(); // pertama kali tampil

        btnNext.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            loadCalendar();
        });

        btnPrev.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            loadCalendar();
        });
//        showRecyclerGrid();
        shimmerSchedule.stopShimmer();
        shimmerSchedule.hideShimmer();

    }


    private void loadCalendar() {

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



//    private void showRecyclerGrid(){
//        rvSchedule.setLayoutManager(new GridLayoutManager(this, 4));
//        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(ScheduleActivity.this);
//        rvSchedule.setAdapter(scheduleAdapter);
//
//
//    }

}