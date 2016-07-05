package com.ningso.ningsodemo;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by NingSo on 16/4/20.下午11:17
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class TestFontProvider extends ContentProvider {

    private final static String LOCAL_DB_NAME = "hifont.db";
    private final static int DB_VERSION = 1;
    //引入mOpenHelper实现第一次创建数据表
    private SQLiteOpenHelper mOpenHelper;
    //用来匹配以后的查询项，根据匹配到的查询项，做相应的查询
    private static UriMatcher URI_MATCHER;
    //授权“域名”,必须唯一，且与AndroidManifest里面注册的须一致
    private final static String AUTHORITY = "LocalShelves";

    //以下是不同查询项的状态码
    private static final int SEARCH = 1;
    private static final int NAMES = 2;
    private static final int NAMES_ID = 3;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        //先将各项注册进去，才能在后面用到后进行匹配
        URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
        URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
        URI_MATCHER.addURI(AUTHORITY, "name", NAMES);
        URI_MATCHER.addURI(AUTHORITY, "name/#", NAMES_ID);
    }

    @Override
    public boolean onCreate() {
        Log.w("pdb", "run here");
        //这块搞了我大半天，所有东西都搞定了，就是表建不出来，原来SqliteHelper的OnCreate()方法不会被自动调用，只有使用getReadableDatabase()后才会新建　　　　 表
        // mOpenHelper = new DatabaseHelper(getContext());
//        mOpenHelper.getReadableDatabase();
//        mOpenHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
