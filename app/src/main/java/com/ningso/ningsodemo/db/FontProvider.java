package com.ningso.ningsodemo.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by NingSo on 16/6/16.下午5:47
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */

public class FontProvider extends ContentProvider {
    private static final int CURRENTS = 0;
    private static final int CURRENTS_ID = 1;
    private static final String TAG = "FontProvider";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private FontDatabaseHelper mSQLiteOpenHelper;

    public static final String FONT_AUTHORITY = "com.android.theme.font.db.info";
    public static final Uri FONT_URI = Uri.parse("content://com.android.theme.font.db.info/current");
    public static final Uri UNLOCK_URI = Uri.parse("content://com.android.theme.unlock.db.info/current");


    static {
        sURIMatcher.addURI(FONT_AUTHORITY, "current", CURRENTS);
        sURIMatcher.addURI(FONT_AUTHORITY, "current/#", CURRENTS_ID);
    }

    public boolean onCreate() {
        this.mSQLiteOpenHelper = FontDatabaseHelper.getInstance(getContext());
        return true;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        SQLiteDatabase writableDatabase = this.mSQLiteOpenHelper.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case CURRENTS /*0*/:
                writableDatabase.delete(FontDatabaseHelper.TABLE_NAME, str, strArr);
                break;
        }
        return CURRENTS;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        long insert = this.mSQLiteOpenHelper.getWritableDatabase().insert(FontDatabaseHelper.TABLE_NAME, null, contentValues);
        if (insert > 0) {
            return ContentUris.withAppendedId(UNLOCK_URI, insert);
        }
        return null;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteDatabase readableDatabase = this.mSQLiteOpenHelper.getReadableDatabase();
        switch (sURIMatcher.match(uri)) {
            case CURRENTS /*0*/:
                return readableDatabase.query(FontDatabaseHelper.TABLE_NAME, strArr, str, strArr2, null, null, str2);
            default:
                return null;
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        SQLiteDatabase writableDatabase = this.mSQLiteOpenHelper.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case CURRENTS /*0*/:
                return writableDatabase.update(FontDatabaseHelper.TABLE_NAME, contentValues, str, strArr);
            default:
                return CURRENTS;
        }
    }

    public String getType(Uri uri) {
        return null;
    }
}