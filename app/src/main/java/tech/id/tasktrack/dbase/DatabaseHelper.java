package tech.id.tasktrack.dbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tech.id.tasktrack.model.Kegiatan;
import tech.id.tasktrack.model.Lokasi;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.model.Schedule;
import tech.id.tasktrack.wifilog.WifiLog;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wifi_monitor.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "wifi_log";
    public static final String TABLE_PEGAWAI = "pegawais";
    public static final String TABLE_SCHEDULE = "schedules";
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

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHEDULE + "(" +
                "id INTEGER PRIMARY KEY, " +
                "tanggal TEXT, " +
                "pegawai_id INTEGER, " +
                "pegawai_name TEXT, " +
                "kegiatan_id INTEGER, " +
                "task TEXT, " +
                "kegiatan_keterangan TEXT, " +
                "lokasi_id INTEGER, " +
                "building TEXT, " +
                "floor TEXT, " +
                "ssid TEXT, " +
                "keterangan TEXT, " +
                "created_by INTEGER, " +
                "created_ip TEXT, " +
                "updated_by INTEGER, " +
                "updated_ip TEXT, " +
                "verifikator_id INTEGER, " +
                "verifikasi_pegawai TEXT, " +        // 'ya' atau 'tidak'
                "verifikasi_verifikator TEXT" +      // 'ya' atau 'tidak'
                ")";

        db.execSQL(CREATE_SCHEDULE_TABLE);
    }

    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("DELETE FROM " + TABLE_PEGAWAI);
        db.execSQL("DELETE FROM " + TABLE_SCHEDULE);

        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEGAWAI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        onCreate(db);
    }


    public List<WifiLog> getAllWifiLogs() {
        List<WifiLog> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM wifi_log ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new WifiLog(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ssid")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ip_address"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public List<WifiLog> getTodayWifiLogs() {
        List<WifiLog> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Ambil tanggal hari ini dalam format yyyy-MM-dd
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new java.util.Date());

        Cursor cursor = db.rawQuery(
                "SELECT * FROM wifi_log WHERE date(timestamp) = ? ORDER BY id DESC",
                new String[]{ today }
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new WifiLog(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ssid")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ip_address"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


    // Insert data schedule
    public void insertSchedule(List<Schedule> schedules) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_SCHEDULE, null, null); // bersihkan data lama

//            for (Schedule s : schedules) {
//                ContentValues cv = new ContentValues();
//                cv.put("id", s.id);
//                cv.put("tanggal", s.tanggal);
//
//                cv.put("pegawai_id", s.pegawai.id);
//                cv.put("pegawai_name", s.pegawai.name);
//
//                cv.put("kegiatan_id", s.kegiatan.id);
//                cv.put("task", s.kegiatan.task);
//                cv.put("kegiatan_keterangan", s.kegiatan.keterangan);
//
//                cv.put("lokasi_id", s.lokasi.id);
//                cv.put("building", s.lokasi.building);
//                cv.put("floor", s.lokasi.floor);
//                cv.put("ssid", s.lokasi.ssid);
//
//                cv.put("keterangan", s.keterangan);
//
//                db.insert(TABLE_SCHEDULE, null, cv);
//            }
            for (Schedule s : schedules) {
                ContentValues cv = new ContentValues();

                cv.put("id", s.id);
                cv.put("tanggal", s.tanggal);

                cv.put("pegawai_id", s.pegawai.id);
                cv.put("pegawai_name", s.pegawai.name);

                cv.put("kegiatan_id", s.kegiatan.id);
                cv.put("task", s.kegiatan.task);
                cv.put("kegiatan_keterangan", s.kegiatan.keterangan);

                cv.put("lokasi_id", s.lokasi.id);
                cv.put("building", s.lokasi.building);
                cv.put("floor", s.lokasi.floor);
                cv.put("ssid", s.lokasi.ssid);

                cv.put("keterangan", s.keterangan);

                // Tambahan
                cv.put("created_by", s.created_by);
                cv.put("created_ip", s.created_ip);
                cv.put("updated_by", s.updated_by);
                cv.put("updated_ip", s.updated_ip);
                cv.put("verifikator_id", s.verifikator_id);
                cv.put("verifikasi_pegawai", s.verifikasi_pegawai);
                cv.put("verifikasi_verifikator", s.verifikasi_verifikator);

                db.insert(TABLE_SCHEDULE, null, cv);
            }


            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertScheduleByMonth(List<Schedule> schedules, int bulan, int tahun) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            // Format tanggal sqlite = yyyy-MM-dd
            String start = String.format("%04d-%02d-01", tahun, bulan);
            String end = String.format("%04d-%02d-31", tahun, bulan);
            // Hapus hanya data bulan & tahun tersebut
            db.delete(TABLE_SCHEDULE,
                    "tanggal >= ? AND tanggal <= ?",
                    new String[]{ start, end }
            );

            // Insert ulang data API ke DB
            for (Schedule s : schedules) {
                ContentValues cv = new ContentValues();
                cv.put("id", s.id);
                cv.put("tanggal", s.tanggal);

                cv.put("pegawai_id", s.pegawai.id);
                cv.put("pegawai_name", s.pegawai.name);

                cv.put("kegiatan_id", s.kegiatan.id);
                cv.put("task", s.kegiatan.task);
                cv.put("kegiatan_keterangan", s.kegiatan.keterangan);

                cv.put("lokasi_id", s.lokasi.id);
                cv.put("building", s.lokasi.building);
                cv.put("floor", s.lokasi.floor);
                cv.put("ssid", s.lokasi.ssid);

                cv.put("keterangan", s.keterangan);

                cv.put("created_by", s.created_by);
                cv.put("created_ip", s.created_ip);
                cv.put("updated_by", s.updated_by);
                cv.put("updated_ip", s.updated_ip);
                cv.put("verifikator_id", s.verifikator_id);
                cv.put("verifikasi_pegawai", s.verifikasi_pegawai);
                cv.put("verifikasi_verifikator", s.verifikasi_verifikator);



                db.insert(TABLE_SCHEDULE, null, cv);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    // Ambil data berdasarkan pegawai
    public List<Schedule> getSchedulesByPegawai(int pegawaiId) {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SCHEDULE + " WHERE pegawai_id = ?",
                new String[]{String.valueOf(pegawaiId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Schedule sc = new Schedule();

                sc.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                sc.tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal"));

                // Pegawai
                Pegawai pg = new Pegawai();
                pg.id = cursor.getInt(cursor.getColumnIndexOrThrow("pegawai_id"));
                pg.name = cursor.getString(cursor.getColumnIndexOrThrow("pegawai_name"));
                sc.pegawai = pg;

                // Kegiatan
                Kegiatan kg = new Kegiatan();
                kg.id = cursor.getInt(cursor.getColumnIndexOrThrow("kegiatan_id"));
                kg.task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
                kg.keterangan = cursor.getString(cursor.getColumnIndexOrThrow("kegiatan_keterangan"));
                sc.kegiatan = kg;

                // Lokasi
                Lokasi lk = new Lokasi();
                lk.id = cursor.getInt(cursor.getColumnIndexOrThrow("lokasi_id"));
                lk.building = cursor.getString(cursor.getColumnIndexOrThrow("building"));
                lk.floor = cursor.getString(cursor.getColumnIndexOrThrow("floor"));
                lk.ssid = cursor.getString(cursor.getColumnIndexOrThrow("ssid"));
                sc.lokasi = lk;

                sc.keterangan = cursor.getString(cursor.getColumnIndexOrThrow("keterangan"));

                sc.created_by = cursor.getInt(cursor.getColumnIndexOrThrow("created_by"));
                sc.created_ip = cursor.getString(cursor.getColumnIndexOrThrow("created_ip"));
                sc.updated_by = cursor.getInt(cursor.getColumnIndexOrThrow("updated_by"));
                sc.updated_ip = cursor.getString(cursor.getColumnIndexOrThrow("updated_ip"));
                sc.verifikator_id = cursor.getInt(cursor.getColumnIndexOrThrow("verifikator_id"));
                sc.verifikasi_pegawai = cursor.getString(cursor.getColumnIndexOrThrow("verifikasi_pegawai"));
                sc.verifikasi_verifikator = cursor.getString(cursor.getColumnIndexOrThrow("verifikasi_verifikator"));


                list.add(sc);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public List<Schedule> getSchedulesByTanggal(int pegawaiId, String tanggal) {
        List<Schedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SCHEDULE +
                        " WHERE pegawai_id = ? AND tanggal = ? " +
                        " ORDER BY tanggal ASC",
                new String[]{ String.valueOf(pegawaiId), tanggal }
        );

        if (cursor.moveToFirst()) {
            do {
                Schedule sc = new Schedule();

                sc.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                sc.tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal"));

                // Pegawai
                Pegawai pg = new Pegawai();
                pg.id = cursor.getInt(cursor.getColumnIndexOrThrow("pegawai_id"));
                pg.name = cursor.getString(cursor.getColumnIndexOrThrow("pegawai_name"));
                sc.pegawai = pg;

                // Kegiatan
                Kegiatan kg = new Kegiatan();
                kg.id = cursor.getInt(cursor.getColumnIndexOrThrow("kegiatan_id"));
                kg.task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
                kg.keterangan = cursor.getString(cursor.getColumnIndexOrThrow("kegiatan_keterangan"));
                sc.kegiatan = kg;

                // Lokasi
                Lokasi lk = new Lokasi();
                lk.id = cursor.getInt(cursor.getColumnIndexOrThrow("lokasi_id"));
                lk.building = cursor.getString(cursor.getColumnIndexOrThrow("building"));
                lk.floor = cursor.getString(cursor.getColumnIndexOrThrow("floor"));
                lk.ssid = cursor.getString(cursor.getColumnIndexOrThrow("ssid"));
                sc.lokasi = lk;

                sc.keterangan = cursor.getString(cursor.getColumnIndexOrThrow("keterangan"));

                sc.created_by = cursor.getInt(cursor.getColumnIndexOrThrow("created_by"));
                sc.created_ip = cursor.getString(cursor.getColumnIndexOrThrow("created_ip"));
                sc.updated_by = cursor.getInt(cursor.getColumnIndexOrThrow("updated_by"));
                sc.updated_ip = cursor.getString(cursor.getColumnIndexOrThrow("updated_ip"));
                sc.verifikator_id = cursor.getInt(cursor.getColumnIndexOrThrow("verifikator_id"));
                sc.verifikasi_pegawai = cursor.getString(cursor.getColumnIndexOrThrow("verifikasi_pegawai"));
                sc.verifikasi_verifikator = cursor.getString(cursor.getColumnIndexOrThrow("verifikasi_verifikator"));

                list.add(sc);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


    public void updateVerifikasiPegawai(int id, int pegawaiId, String tanggal, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put("verifikasi_pegawai", status);

        if (status == null || status.equals("tidak")) {
            cv.putNull("verifikasi_pegawai");
        } else {
            cv.put("verifikasi_pegawai", status);
        }

        db.update(TABLE_SCHEDULE, cv,
                "id = ? AND pegawai_id = ? AND tanggal = ?",
                new String[]{ String.valueOf(id), String.valueOf(pegawaiId), tanggal }
        );
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
    public Pegawai getPegawai(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PEGAWAI + " WHERE id = "+id+" LIMIT 1";

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