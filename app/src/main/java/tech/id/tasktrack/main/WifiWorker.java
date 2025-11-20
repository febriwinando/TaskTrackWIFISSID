package tech.id.tasktrack.main;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
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
    public static final String CHANNEL_ID = "wifi_monitor_channel";
    Context context;

    public WifiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @NonNull
    @Override
    public Result doWork() {

        createNotificationChannel();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID();
        int ip = wifiInfo.getIpAddress();

        String ipAddress = String.format(Locale.US, "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        String ipadd = "";
        if (ssid != null && ssid.equals("<unknown ssid>")) {
            ssid = "Tidak Terhubung";
            ipadd = "";
        } else if (ssid != null) {
            ssid = "SSID: "+ssid.replace("\"", "");
            ipadd = " | IP: " + ipAddress;
        }

        DatabaseHelper db = new DatabaseHelper(context);
        db.insertWifiLog(time, ssid, ipAddress);

        // kirim notifikasi hanya saat berhasil menyimpan
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.greenlogo)
                        .setContentTitle("WiFi Tersimpan")
                        .setContentText( ssid + " " +ipadd)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "WiFi Monitor",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }
}

//public class WifiWorker extends Worker {
//    public static final String CHANNEL_ID = "wifi_monitor_channel";
//    Context context;
//    public WifiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        this.context = context;
//    }
//
//    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        createNotificationChannel();
//
//        WifiManager wifiManager = (WifiManager) getApplicationContext()
//                .getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//        String ssid = wifiInfo.getSSID();
//        int ip = wifiInfo.getIpAddress();
//        String ipAddress = String.format(Locale.US, "%d.%d.%d.%d",
//                (ip & 0xff), (ip >> 8 & 0xff),
//                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
//
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
//                Locale.getDefault()).format(new Date());
//
//        if (ssid != null && ssid.equals("<unknown ssid>")) {
//            ssid = "Tidak Terhubung";
//        } else if (ssid != null) {
//            ssid = ssid.replace("\"", "");
//        }
//
//        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
//        db.insertWifiLog(time, ssid, ipAddress);
//
//
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.logott)
//                        .setContentTitle("Connected")
//                        .setContentText("SSID: " + ssid + " | IP: " + ipAddress)
//                        .setPriority(NotificationCompat.PRIORITY_LOW);
//
//        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
//
//        return Result.success();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "WiFi Worker Notification",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            NotificationManager manager =
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            if (manager != null) {
//                manager.createNotificationChannel(channel);
//            }
//        }
//    }
//
//}
