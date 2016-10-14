package com.ningso.ningsodemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ningso.ningsodemo.utils.DownLoadHelper;
import com.ningso.ningsodemo.utils.PermissionHelper;
import com.ningso.shortcutbadger.ShortcutBadger;

import java.io.File;


public class MainActivity extends AppCompatActivity implements PermissionHelper.OnApplyPermissionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView mImageView;
    TextView mTextView;
    private PermissionHelper mPermissionHelper;
    int badgeCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mImageView = (ImageView) findViewById(R.id.iv_img);
        mTextView = (TextView) findViewById(R.id.tv_demo);
        Button add = (Button) findViewById(R.id.btn_add);
        Button update = (Button) findViewById(R.id.btn_update);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, ShortcutBadger.applyCount(MainActivity.this, badgeCount++) ? "badge add success" : "badge add false", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, ShortcutBadger.removeCount(MainActivity.this) ? "badge remove success" : "badge remove false", Toast.LENGTH_SHORT).show();
            }
        });
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
            } else {

                Log.d(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
                DownLoadHelper.executeDownLoad("http://upaicdn.xinmei365.com/fontAPK/QRhJjdPc.apk", path, "ningso.apk",
                        new DownLoadHelper.FileCallback() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void inProgress(float progress) {
                                Log.d(TAG, "progress: " + progress);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "request: " + "Exception :" + e);
                            }

                            @Override
                            public void onResponse(File response) {
                                installPackage(getApplicationContext(), response);
                            }
                        });
            }
        });
        getLuauncher();
    }

    private void getLuauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
        mTextView.setText(currentHomePackage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void installPackage(Context context, File apkfile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);//通过FileProvider创建一个content类型的Uri
                Log.d("MainActivity", contentUri.toString());
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAfterApplyAllPermission() {

    }
}
