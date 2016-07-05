package com.ningso.ningsodemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private static final String FONT_URI = "content://com.android.theme.font.db.info/current";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextView = (TextView) findViewById(R.id.tv_Text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = getContentResolver();
                Uri uri = Uri.parse(FONT_URI);
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //这个必须是列名哦哦哦
                        System.out.println("外部访问***************************************:\n"
                                + "\n_id: " + cursor.getString(0)
                                + "\nuid: " + cursor.getString(1)
                                + "\nstate: " + cursor.getString(2)
                                + "\nfilename: " + cursor.getString(3)
                                + "\ntype: " + cursor.getString(4)
                                + "\nname: " + cursor.getString(5)
                                + "\ndownloadId: " + cursor.getString(6)
                                + "\ndownload_time: " + cursor.getString(7)
                                + "\ndownloaded_times: " + cursor.getString(8)
                                + "\npraised_times: " + cursor.getString(9)
                                + "\npraised: " + cursor.getString(10)
                                + "\nedition: " + cursor.getString(11)
                                + "\nprice: " + cursor.getString(12)
                                + "\nopenid: " + cursor.getString(13)
                                + "\nverify: " + cursor.getString(14)
                        );

                    }
                    cursor.close();
                } else {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                //addShortCut(MainActivity.this);

//                PulseChecker.makePulse(MainActivity.this, "com.xinmei365.font/com.xinmei365.font.ui.activity.SplashActivity",
//                        PulseChecker.CPNT_TYPE_ACTIVITY, 10);
                //startActivity(new Intent(MainActivity.this, NewAppWidgetConfigureActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_demo1:
                insetDB();
                break;
            case R.id.action_demo2:
                updateDB();
                break;
            case R.id.action_demo3:
                deleteDB();
                break;
            case R.id.action_demo4:
                changeFont();
                break;
            case R.id.action_demo5:

                break;
            case R.id.action_demo6:

                break;
            case R.id.action_demo7:

                break;
            case R.id.action_demo8:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insetDB() {
        ContentResolver insertcontentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.theme.font.db.info/current");
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", 754);
        contentValues.put("state", 0);
        contentValues.put("filename", "/data/bbkcore/theme/.dwd/c/o/m/b/b/k/t/h/e/m/e/F/fontthree.txj");
        contentValues.put("type", 0);
        contentValues.put("name", "宁平平字体");
//        contentValues.put("downloadId", 0);
//        contentValues.put("download_time", 0);
//        contentValues.put("downloaded_times", null);
        contentValues.put("praised", -1);
        contentValues.put("edition", 1);
        contentValues.put("price", 0);
        contentValues.put("openid", "vivo");
        contentValues.put("verify", 1);

        Log.e("######", "insert:=====: " + insertcontentResolver.insert(uri, contentValues));
        insertcontentResolver.notifyChange(Uri.parse(FONT_URI), null);

    }

    private void updateDB() {
        ContentResolver insertcontentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.theme.font.db.info/current");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "宁平平字体");
        String whereClause = "_id=?";
        String[] whereArgs = {"1"};
        Log.e("######", "upate:=====: " + insertcontentResolver.update(uri, contentValues, whereClause, whereArgs));
    }

    private void deleteDB() {
        Log.e("######", "delete:=====: " + getContentResolver().delete(Uri.parse(FONT_URI), null, null));
    }

    private void changeFont() {
        File file = new File("/data/bbkcore/theme/.dwd/c/o/m/b/b/k/t/h/e/m/e/F/星空体.txj");
        if (file.exists()) {
            mTextView.setTypeface(Typeface.createFromFile(file));
        } else {
            Log.e("######", "changeFont:=====: ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
