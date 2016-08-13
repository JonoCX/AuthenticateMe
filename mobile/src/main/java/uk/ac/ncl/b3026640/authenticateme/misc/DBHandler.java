package uk.ac.ncl.b3026640.authenticateme.misc;

import android.content.Context;
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

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " +
                TABLE + " (" + SOCIAL_ID + " INTEGER PRIMARY KEY," +
                SOCIAL_METHOD + " TEXT)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
