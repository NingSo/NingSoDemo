package com.ningso.ningsodemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.ningso.ningsodemo.downloadmanager.bizs.DBManager;
import com.ningso.ningsodemo.downloadmanager.cons.PublicCons;

import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private final static String TAG = "[DBHelper]";
    private static final String DB_NAME = "mydata.db"; // 数据库名称
    private static final int VERSION = 15; // 数据库版本

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private final static String CREATE_HASH_TABLE =
            "create table hash(key varchar(64) not null , value varchar(10240) not null );";
    public final static String CREATE_FORMULA_TABLE =
            "create table formula(id varchar(128) not null, name varchar(256), category varchar(256),"
                    + " type varchar(32), preview varchar(256), content varchar(10240), parent varchar(128), keywords varchar(10240), timestamp varchar(64), sync integer default 0);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE_HASH_TABLE);

            db.execSQL(CREATE_FORMULA_TABLE);
            //应用内推荐App下载相关
            db.execSQL(PublicCons.DBCons.TB_TASK_SQL_CREATE);
            db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_CREATE);

            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {

            //应用内推荐App下载相关
            DBManager.onUpgrade(db, oldVersion, newVersion);

            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setLocale(Locale.CHINA);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
