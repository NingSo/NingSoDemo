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

import com.ningso.ningsodemo.utils.FileUitls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
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

        //new BusyBoxThread(getApplication()).start();
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
                // new RootThread().start();
                FileUitls.CopyAssertJarToFile(this, "silence.jar", "silence.jar");
                break;
            case R.id.action_demo2:
                //  downloadaAndInstallApk();
                loadDex();
                break;
            case R.id.action_demo3:
                //  ShellUtils.copyFile2SystemLib("/data/data/com.mycheering.apps/lib");
                // Toast.makeText(MainActivity.this, "getInstallLoacation:" + PackageUtils.getInstallLocation(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo4:
                // Toast.makeText(MainActivity.this, "UnSilentInstall:" + PackageUtils.uninstallSilent(getApplicationContext(), "com.ningso.redenvelope", false), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo5:
                //  PackageUtils.startInstalledAppDetails(getApplicationContext(), "com.ningso.redenvelope");
                break;
            case R.id.action_demo6:
                new InStallSilent().start();
                break;
            case R.id.action_demo7:
//                if (ShellUtils.CopyApkSystem(Environment.getExternalStorageDirectory() + File.separator + "app-release.apk")) {
//                    new InStallSilent().start();
//                } else {
//                    Toast.makeText(MainActivity.this, "Copy: faile", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.action_demo8:
//                Toast.makeText(MainActivity.this, "Root: " + ShellUtils.isRootSystem(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // 定义DexClassLoader
    // 第一个参数：是dex压缩文件的路径
    // 第二个参数：是dex解压缩后存放的目录
    // 第三个参数：是C/C++依赖的本地库文件目录,可以为null
    // 第四个参数：是上一级的类加载器
    private void loadDex() {
        File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "classes.dex");
        File dexOutputDir = getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader classLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                dexOutputDir.getAbsolutePath(), null, getClassLoader());
        Class iclass;
        try {
            iclass = classLoader.loadClass("com.ningso.silence.ShellUtils");
            Method addmethod = iclass.getMethod("CopyApkSystem", String.class);
            Boolean install = (Boolean) addmethod.invoke(iclass, Environment.getExternalStorageDirectory() + File.separator + "app-release.apk");
            Log.e("ee", "eee: " + install);
            Method[] methods = iclass.getMethods();

            Object instance = iclass.newInstance();
            Method method = iclass.getMethod("isRootSystem", new Class[]{});
            Boolean isRoot = (Boolean) method.invoke(instance);
            Toast.makeText(MainActivity.this, "Root: " + isRoot, Toast.LENGTH_SHORT).show();

            iclass.getMethod("deleteDir", File.class).invoke(instance, getDir("dex", Context.MODE_PRIVATE));


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void removeAppDexFile() {
        File dexOutputDir = getDir("classes", Context.MODE_PRIVATE);
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
//            Message message = new Message();
//            int installsuccuess = PackageUtils.installSilent(getApplicationContext(), ShellUtils.getInstallSilentDir() + "demo.apk");
//            //  boolean hassuccess = ApkController.install("/system/priv-app/" + "demo.apk", getApplicationContext());
//            if (installsuccuess == 1) {
//                message.what = HAS_INSTALL_SUCCESS;
//            } else {
//                message.what = HAS_INSTALL_FAIL;
//            }
//            //   Log.e("DEBUG", "installsuccuess:" + installsuccuess);
//            handler.sendMessage(message);
        }
    }

    private void downloadaAndInstallApk() {
        OkHttpUtils.get()//
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
//                        if (PackageUtils.installSilent(MainActivity.this, file.getAbsolutePath()) == 1) {
//                            Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
    }

}
