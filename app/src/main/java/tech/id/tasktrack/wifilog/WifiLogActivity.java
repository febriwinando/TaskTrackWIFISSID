package tech.id.tasktrack.wifilog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.id.tasktrack.R;
import tech.id.tasktrack.dbase.DatabaseHelper;

public class WifiLogActivity extends AppCompatActivity {

    ImageView ivBackWifiLog;
    RecyclerView rvWifiLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wifi_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBackWifiLog = findViewById(R.id.ivBackWifiLog);
        rvWifiLog = findViewById(R.id.rvWifiLog);
        rvWifiLog.setHasFixedSize(true);

        DatabaseHelper db = new DatabaseHelper(this);
        // load data dari sqlite
        List<WifiLog> logs = db.getAllWifiLogs();

        rvWifiLog.setLayoutManager(new LinearLayoutManager(this));
        WifiLogAdapter adapter = new WifiLogAdapter(this, logs);
        rvWifiLog.setAdapter(adapter);


        // kirim ke adapter
//        adapter.setData(logs);

        ivBackWifiLog.setOnClickListener(new View.OnClickListener() {
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