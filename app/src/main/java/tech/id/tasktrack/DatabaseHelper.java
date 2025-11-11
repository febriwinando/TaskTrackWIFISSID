package tech.id.tasktrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wifi_monitor.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "wifi_log";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp TEXT, " +
                "ssid TEXT, " +
                "ip_address TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertWifiLog(String timestamp, String ssid, String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timestamp", timestamp);
        values.put("ssid", ssid);
        values.put("ip_address", ip);

        long result = db.insert(TABLE_NAME, null, values);
        if (result != -1) {
            Log.d("DB_LOG", "Data tersimpan: " + ssid + " | " + ip);
        } else {
            Log.e("DB_LOG", "Gagal menyimpan data");
        }
    }

    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
    }
}