package com.ningso.ningsodemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NingSo on 16/6/16.下午5:47
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */

public class FontDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "font.db";
    public static final String TABLE_NAME = "font";
    private static final int VERSION = 1;
    private static FontDatabaseHelper mInstance = null;

    static synchronized FontDatabaseHelper getInstance(Context context) {
        FontDatabaseHelper fontDatabaseHelper;
        synchronized (FontDatabaseHelper.class) {
            if (mInstance == null) {
                mInstance = new FontDatabaseHelper(context);
            }
            fontDatabaseHelper = mInstance;
        }
        return fontDatabaseHelper;
    }

    public FontDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        CreateTable(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        DropTable(sQLiteDatabase);
        CreateTable(sQLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        DropTable(sQLiteDatabase);
        CreateTable(sQLiteDatabase);
    }

    private void CreateTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE font (_id INTEGER PRIMARY KEY,uid TEXT,state INTEGER,filename TEXT,type INTEGER,name TEXT,downloadId LONG,download_time LONG,downloaded_times LONG,praised_times LONG,praised INTEGER DEFAULT -1,edition INTEGER,price FLOAT DEFAULT 0,openid TEXT DEFAULT 'vivo',verify INTEGER DEFAULT 0 );");
    }

    private void DropTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS font");
    }
}