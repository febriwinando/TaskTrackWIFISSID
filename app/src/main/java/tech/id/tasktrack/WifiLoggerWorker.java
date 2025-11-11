package tech.id.tasktrack;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WifiLoggerWorker extends Worker {

    private static final String TAG = "WifiLoggerWorker";

    public WifiLoggerWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = wifiInfo.getSSID();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = Formatter.formatIpAddress(ipAddress);
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // Simpan ke database
            WifiDatabaseHelper dbHelper = new WifiDatabaseHelper(context);
            dbHelper.insertLog(currentTime, ssid, ip);

            // Tampilkan di Logcat
            Log.d(TAG, "WiFi Log: Time=" + currentTime + ", SSID=" + ssid + ", IP=" + ip);
        } else {
            Log.d(TAG, "WiFi is disabled or not connected.");
        }

        return Result.success();
    }
}