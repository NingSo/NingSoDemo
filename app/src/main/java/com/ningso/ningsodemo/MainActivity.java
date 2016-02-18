package com.ningso.ningsodemo;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ningso.ningsodemo.utils.ApkController;
import com.ningso.ningsodemo.utils.BusyBoxThread;
import com.ningso.ningsodemo.utils.PackageUtils;
import com.ningso.ningsodemo.utils.ShellUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int HAS_ROOT_SUCCESS = 0x0001;
    private static final int HAS_ROOT_FAIL = 0x0002;
    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAS_ROOT_SUCCESS:
                    Toast.makeText(getApplicationContext(), "Root: True", Toast.LENGTH_SHORT).show();
                    break;
                case HAS_ROOT_FAIL:
                    Toast.makeText(getApplicationContext(), "Root: False", Toast.LENGTH_SHORT).show();
                    break;
                case HAS_INSTALL_SUCCESS:
                    Toast.makeText(getApplicationContext(), "Install: True", Toast.LENGTH_SHORT).show();
                    break;
                case HAS_INSTALL_FAIL:
                    Toast.makeText(getApplicationContext(), "Install: False", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new BusyBoxThread(getApplication()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_demo1:
                new RootThread().start();
                break;
            case R.id.action_demo2:
                downloadaAndInstallApk();
                break;
            case R.id.action_demo3:
                // Toast.makeText(MainActivity.this, "getInstallLoacation:" + PackageUtils.getInstallLocation(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo4:
                // Toast.makeText(MainActivity.this, "UnSilentInstall:" + PackageUtils.uninstallSilent(getApplicationContext(), "com.ningso.redenvelope", false), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo5:
                PackageUtils.startInstalledAppDetails(getApplicationContext(), "com.ningso.redenvelope");
                break;
            case R.id.action_demo6:
                new InStallSilent().start();
                break;
            case R.id.action_demo7:
                if (ShellUtils.CopyApkSystem(Environment.getExternalStorageDirectory() + File.separator + "app-release.apk", "demo")) {
                    new InStallSilent().start();
                } else {
                    Toast.makeText(MainActivity.this, "Copy: faile", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_demo8:
                Toast.makeText(MainActivity.this, "Root: " + ShellUtils.isRootSystem(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class RootThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            boolean hasroot = false;//ShellUtils.checkRootPermission();
            if (hasroot) {
                msg.what = HAS_ROOT_SUCCESS;
            } else {
                msg.what = HAS_ROOT_FAIL;
            }
            handler.sendMessage(msg);
        }
    }

    class InStallSilent extends Thread {
        @Override
        public void run() {
            super.run();
            Message message = new Message();
            int installsuccuess = PackageUtils.installSilent(getApplicationContext(), "system/app/Demo.apk");
            //  boolean hassuccess = ApkController.install(Environment.getExternalStorageDirectory().getAbsolutePath() + "/app-release.apk", getApplicationContext())
            if (installsuccuess == 0) {
                message.what = HAS_INSTALL_SUCCESS;
            } else {
                message.what = HAS_INSTALL_FAIL;
            }
            //   Log.e("DEBUG", "installsuccuess:" + installsuccuess);
            handler.sendMessage(message);
        }
    }

    private void downloadaAndInstallApk() {
        OkHttpUtils.get()//
                .url("http://upaicdn.xinmei365.com/newwfs/support/ShuameMobile.apk")//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "Download/app-release.apk")//
                {
                    @Override
                    public void inProgress(float progress) {
                        Log.e("", "onResponse :" + (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(File file) {
//                        if (PackageUtils.installSilent(MainActivity.this, file.getAbsolutePath()) == 1) {
//                            Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
    }

}
