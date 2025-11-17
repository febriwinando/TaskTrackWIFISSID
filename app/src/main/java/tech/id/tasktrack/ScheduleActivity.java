package tech.id.tasktrack;

import android.os.Bundle;
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

public class ScheduleActivity extends AppCompatActivity {

    ShimmerFrameLayout shimmerSchedule;
    RecyclerView rvSchedule;
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

        shimmerSchedule = findViewById(R.id.shimmerSchedule);
        rvSchedule = findViewById(R.id.rvSchedule);
        rvSchedule.setHasFixedSize(true);

        showRecyclerGrid();
        shimmerSchedule.stopShimmer();
        shimmerSchedule.hideShimmer();

    }

    private void showRecyclerGrid(){
        rvSchedule.setLayoutManager(new GridLayoutManager(this, 4));
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(ScheduleActivity.this);
        rvSchedule.setAdapter(scheduleAdapter);


    }

}