package tech.id.tasktrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class WifiDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wifi_logs.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "wifi_log";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_SSID = "ssid";
    public static final String COLUMN_IP = "ip";

    public WifiDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_SSID + " TEXT, "
                + COLUMN_IP + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertLog(String time, String ssid, String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_SSID, ssid);
        values.put(COLUMN_IP, ip);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}