package tech.id.tasktrack.main;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tech.id.tasktrack.R;
import tech.id.tasktrack.dbase.DatabaseHelper;

public class WifiWorker extends Worker {

    Context context;
    public WifiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID();
        int ip = wifiInfo.getIpAddress();
//        String ipAddress = String.format(Locale.US, "%d.%d.%d.%d",
//                (ip & 0xff), (ip >> 8 & 0xff),
//                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        String ipAddress = String.format(Locale.US, "%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff));

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());

        if (ssid != null && ssid.equals("<unknown ssid>")) {
            ssid = "Tidak Terhubung";
        } else if (ssid != null) {
            ssid = ssid.replace("\"", "");
        }

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.insertWifiLog(time, ssid, ipAddress);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, WifiMonitorService.CHANNEL_ID)
                        .setSmallIcon(R.drawable.logott)
                        .setContentTitle("WiFi Checked")
                        .setContentText("SSID: " + ssid + " | IP: " + ipAddress)
                        .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }
}
