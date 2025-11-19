//package tech.id.tasktrack.main;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import tech.id.tasktrack.dbase.DatabaseHelper;
//import tech.id.tasktrack.R;
//
//
//public class WifiMonitorService extends Service {
//
//    private Handler handler;
//    private Runnable wifiCheckTask;
//    private DatabaseHelper dbHelper;
//
//    // interval 15 menit
//    private static final int INTERVAL = 15 * 60 * 1000;
//    private static final String CHANNEL_ID = "wifi_monitor_channel";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        dbHelper = new DatabaseHelper(this);
//        handler = new Handler();
//        createNotificationChannel();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Buat notifikasi awal saat service mulai
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("WiFi Monitor Aktif")
//                .setContentText("Mencatat koneksi WiFi setiap 15 menit")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setOngoing(true)
//                .build();
//
//        startForeground(1, notification);
//
//        startWifiCheckTask();
//
//        return START_STICKY;
//    }
//
//    private void startWifiCheckTask() {
//        wifiCheckTask = new Runnable() {
//            @Override
//            public void run() {
//                checkWifiConnection();
//                handler.postDelayed(this, INTERVAL); // jadwalkan ulang
//            }
//        };
//        handler.post(wifiCheckTask);
//    }
//
//    private void checkWifiConnection() {
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//        if (wifiInfo == null) return;
//
//        String ssid = wifiInfo.getSSID();
//        int ip = wifiInfo.getIpAddress();
//        String ipAddress = String.format(Locale.US, "%d.%d.%d.%d",
//                (ip & 0xff), (ip >> 8 & 0xff),
//                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
//
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
//
//        if (ssid != null && ssid.equals("<unknown ssid>")) {
//            ssid = "Tidak Terhubung";
//        } else if (ssid != null) {
//            ssid = ssid.replace("\"", ""); // hapus tanda kutip dari SSID
//        }
//
//        // Simpan ke database
//        dbHelper.insertWifiLog(time, ssid, ipAddress);
//
//        // Logcat debug
//        Log.d("WiFiMonitor", "Jam: " + time + " | SSID: " + ssid + " | IP: " + ipAddress);
//
//        // Update notifikasi setiap kali dicek
//        updateNotification(ssid, time);
//    }
//
//    private void updateNotification(String ssid, String time) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager == null) return;
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("WiFi Aktif: " + ssid)
//                .setContentText("Terakhir dicek: " + time)
//                .setSmallIcon(R.drawable.logott)
//                .setOngoing(true)
//                .build();
//
//        notificationManager.notify(1, notification);
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "WiFi Monitor Service",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            channel.setDescription("Notifikasi WiFi setiap 15 menit");
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) manager.createNotificationChannel(channel);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        handler.removeCallbacks(wifiCheckTask);
//        super.onDestroy();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}



package tech.id.tasktrack.main;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import tech.id.tasktrack.R;

public class WifiMonitorService extends Service {

    public static final String CHANNEL_ID = "wifi_monitor_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WiFi Tracking Active")
                .setContentText("Memantau koneksi WiFi setiap 15 menit")
                .setSmallIcon(R.drawable.logott)
                .build();

        startForeground(1, notification);

        PeriodicWorkRequest wifiWork =
                new PeriodicWorkRequest.Builder(WifiWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueue(wifiWork);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "WiFi Monitor Service",
                            NotificationManager.IMPORTANCE_LOW
                    );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
