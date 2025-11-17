package tech.id.tasktrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tech.id.tasktrack.model.Pegawai;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wifi_monitor.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "wifi_log";
    public static final String TABLE_PEGAWAI = "pegawais";

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

        String createPegawaiTable = "CREATE TABLE " + TABLE_PEGAWAI + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "nik TEXT, " +
                "employee_id TEXT, " +
                "email TEXT, " +
                "nomor_wa TEXT, " +
                "level TEXT, " +
                "status TEXT, " +
                "inactive_reason TEXT, " +
                "foto TEXT" +
                ")";

        db.execSQL(createPegawaiTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEGAWAI);
        onCreate(db);
    }

    // --------------------------------------------------------------------
    // INSERT DATA PEGAWAI
    // --------------------------------------------------------------------
    public void insertPegawai(Pegawai p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PEGAWAI, null, null); // Clear old login first (opsional)

        ContentValues cv = new ContentValues();
        cv.put("id", p.id);
        cv.put("name", p.name);
        cv.put("nik", p.nik);
        cv.put("employee_id", p.employee_id);
        cv.put("email", p.email);
        cv.put("nomor_wa", p.nomor_wa);
        cv.put("level", p.level);
        cv.put("status", p.status);
        cv.put("inactive_reason", p.inactive_reason);
        cv.put("foto", p.foto);

        db.insert(TABLE_PEGAWAI, null, cv);
        db.close();
    }

    // GET data pegawai
    public Pegawai getPegawai() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PEGAWAI + " LIMIT 1";

        var cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            Pegawai p = new Pegawai();
            p.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            p.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            p.nik = cursor.getString(cursor.getColumnIndexOrThrow("nik"));
            p.employee_id = cursor.getString(cursor.getColumnIndexOrThrow("employee_id"));
            p.email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            p.nomor_wa = cursor.getString(cursor.getColumnIndexOrThrow("nomor_wa"));
            p.level = cursor.getString(cursor.getColumnIndexOrThrow("level"));
            p.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            p.inactive_reason = cursor.getString(cursor.getColumnIndexOrThrow("inactive_reason"));
            p.foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"));

            cursor.close();
            db.close();
            return p;
        }
        cursor.close();
        db.close();
        return null;
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