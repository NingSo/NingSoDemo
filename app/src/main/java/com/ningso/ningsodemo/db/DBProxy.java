package com.ningso.ningsodemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ningso.ningsodemo.NingSoApp;
import com.ningso.ningsodemo.utils.ILog;

public class DBProxy {

    private final static String TABLE = "hash";

    public static final int DEFAULT_INT_VALUE = -1;

    private static volatile SQLiteDatabase INSTANCE;

    public static SQLiteDatabase getDatabase() {
        if (INSTANCE == null) {
            synchronized (DBProxy.class) {
                if (INSTANCE == null) {
                    DBHelper helper = new DBHelper(NingSoApp.getInstance());
                    INSTANCE = helper.getWritableDatabase();
                }
            }
        }
        return INSTANCE;
    }

    public synchronized static void init() {
        try {
            getDatabase();
        } catch (Exception e) {
            ILog.e(e.getMessage());
        }
    }

    public static boolean update_data(String key, String val) {
        if (getDatabase() == null || key == null || val == null) {
            return false;
        }

        // check whether key exists first
        String v = get_data(key);
        if (v != null) {
            ContentValues cv = new ContentValues();
            cv.put("value", val);
            String whereClause = "key=?";
            String[] whereArgs = {key};
            getDatabase().update(TABLE, cv, whereClause, whereArgs);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("key", key);
            cv.put("value", val);
            getDatabase().insert(TABLE, null, cv);
        }
        return true;
    }

    public static void delete_data(String key) {
        if (key == null || getDatabase() == null) {
            return;
        }
        String whereClause = "key=?";
        String[] whereArgs = {key};
        getDatabase().delete(TABLE, whereClause, whereArgs);
    }

    public static String get_data(String key) {
        if (key == null || getDatabase() == null)
            return null;
        Cursor c = getDatabase().rawQuery("select * from hash where key=?", new String[]{key});
        String r = null;
        if (c.moveToFirst()) {
            r = c.getString(c.getColumnIndex("value"));
        }
        c.close();
        return r;
    }

    public static int get_int_data(String key) {
        String c = get_data(key);

        if (c != null) {
            try {
                return Integer.parseInt(c);
            } catch (Exception e) {
                return DBProxy.DEFAULT_INT_VALUE;
            }
        } else
            return DBProxy.DEFAULT_INT_VALUE;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.close();
        }
    }
}
