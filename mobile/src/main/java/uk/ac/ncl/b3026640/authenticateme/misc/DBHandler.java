package uk.ac.ncl.b3026640.authenticateme.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jonathan on 12/08/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "social-pref";
    private static final String TABLE = "socialpref";

    private static final String SOCIAL_ID = "id";
    private static final String SOCIAL_METHOD = "method";

    private static final String GET_DATA = "SELECT * FROM " + TABLE;
    private static final String GET_DATA_ID = "SELECT * FROM " + TABLE + " WHERE " + SOCIAL_ID + "=";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " +
                TABLE + " (" + SOCIAL_ID + " INTEGER PRIMARY KEY," +
                SOCIAL_METHOD + " TEXT)";
        db.execSQL(create);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
        db.close();
    }

    public boolean insert(Long id, String method) {
        ContentValues values = new ContentValues();
        values.put("id", id.intValue());
        values.put("method", method);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE, null, values);
        db.close();
        return true;
    }

    public boolean ifExists(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getData(id);
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(GET_DATA, null);
    }

    public Cursor getData(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(GET_DATA_ID + "" + id, null);
    }
}
