package com.ningso.ningsodemo;

import android.content.Context;
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

import com.ningso.jni.HttpUtils;
import com.ningso.ningsodemo.utils.DexClassLoadProxy;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int HAS_ROOT_SUCCESS = 0x0001;
    private static final int HAS_ROOT_FAIL = 0x0002;
    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;

    static {
        System.loadLibrary("datagetter");
    }

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
//packageKey=sByXii1W&categoryKey=KeT3axbI&ResourcesTypeKey=NHwpZYrz&adType=image
//                发送下载成功日志
//                String[] keys = new String[]{
//                        "packageKey",
//                        "categoryKey",
//                        "ResourcesTypeKey",
//                        "adType"
//                };
//                String[] values = new String[]{
//                        "sByXii1W",
//                        "KeT3axbI",
//                        "NHwpZYrz",
//                        "image"
//                };
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("packageKey", "sByXii1W");
                paramMap.put("categoryKey", "KeT3axbI");
                paramMap.put("ResourcesTypeKey", "NHwpZYrz");
                paramMap.put("adType", "image");
                try {
                    Log.e("###", "str: " + HttpUtils.AsycPostData("ad/getAdOne", paramMap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                Boolean hasRoot = (Boolean) DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.ShellUtils", "isRootSystem", false);
                Toast.makeText(MainActivity.this, "hasRoot:" + hasRoot, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo2:
                Boolean results = (Boolean) DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.ShellUtils", "CopyApkToSystem", true,
                                Environment.getExternalStorageDirectory() + File.separator + "app-release.apk");
                Toast.makeText(MainActivity.this, "CopyApkToSystem:" + results, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo3:
                int installLoacation = (int) DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.PackageUtils", "getInstallLocation", true);
                //  ShellUtils.copyFile2SystemLib("/data/data/com.mycheering.apps/lib");
                Toast.makeText(MainActivity.this, "getInstallLoacation:" + installLoacation, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo4:
                int uninstallSilent = (int) DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.PackageUtils", "uninstallSilent", true,
                                getApplicationContext(), "com.ningso.redenvelope", false);

                Toast.makeText(MainActivity.this, "uninstallSilent:" + uninstallSilent, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo5:
                DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.PackageUtils", "startInstalledAppDetails", true,
                                getApplicationContext(), "com.ningso.redenvelope");
                //  PackageUtils.startInstalledAppDetails(getApplicationContext(), "com.ningso.redenvelope");
                break;
            case R.id.action_demo6:
                new InStallSilent().start();
                break;
            case R.id.action_demo7:
                Boolean results7 = (Boolean) DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.ShellUtils", "CopyApkToSystem", true,
                                Environment.getExternalStorageDirectory() + File.separator + "app-release.apk");
                if (results7) {
                    new InStallSilent().start();
                } else {
                    Toast.makeText(MainActivity.this, "Copy: faile", Toast.LENGTH_SHORT).show();
                }
//                if (ShellUtils.CopyApkSystem(Environment.getExternalStorageDirectory() + File.separator + "app-release.apk")) {
//                    new InStallSilent().start();
//                } else {
//                    Toast.makeText(MainActivity.this, "Copy: faile", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.action_demo8:
                DexClassLoadProxy.getInstance()
                        .executeClass("com.ningso.silence.ShellUtils", "deleteDir", false,
                                getDir("dex", Context.MODE_PRIVATE));
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
            int installsuccuess = 0;
            String installpath = (String) DexClassLoadProxy.getInstance().executeClass("com.ningso.silence.ShellUtils", "getInstallSilentDir", true);
            if (installpath != null) {
                installsuccuess = (int) DexClassLoadProxy.getInstance().executeClass("com.ningso.silence.PackageUtils", "installSilent", true, getApplicationContext(), installpath + "app-release.apk");
            }
            // int installsuccuess = PackageUtils.installSilent(getApplicationContext(), ShellUtils.getInstallSilentDir() + "demo.apk");
            //  boolean hassuccess = ApkController.install("/system/priv-app/" + "demo.apk", getApplicationContext());
            if (installsuccuess == 1) {
                message.what = HAS_INSTALL_SUCCESS;
            } else {
                message.what = HAS_INSTALL_FAIL;
            }
            handler.sendMessage(message);
        }
    }

    private void downloadaAndInstallApk() {
        OkHttpUtils.get()
                .url("http://upaicdn.xinmei365.com/newwfs/support/ShuameMobile.apk")//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "Download/app-release.apk") {
                    @Override
                    public void inProgress(float progress) {
                        Log.e("", "onResponse :" + (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(File file) {
                    }
                });
    }

}
