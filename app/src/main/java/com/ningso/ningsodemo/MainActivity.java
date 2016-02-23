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

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int HAS_ROOT_SUCCESS = 0x0001;
    private static final int HAS_ROOT_FAIL = 0x0002;
    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;
    private DexClassLoader classLoader;


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
                Boolean hasRoot = (Boolean) executeClass("com.ningso.silence.ShellUtils", "isRootSystem", false);
                Toast.makeText(MainActivity.this, "CopyApkSystem:" + hasRoot, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo2:
                Boolean results = (Boolean) executeClass("com.ningso.silence.ShellUtils", "CopyApkSystem", true,
                        Environment.getExternalStorageDirectory() + File.separator + "app-release.apk");
                Toast.makeText(MainActivity.this, "CopyApkSystem:" + results, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo3:
                int installLoacation = (int) executeClass("com.ningso.silence.PackageUtils", "getInstallLocation", true);
                //  ShellUtils.copyFile2SystemLib("/data/data/com.mycheering.apps/lib");
                Toast.makeText(MainActivity.this, "getInstallLoacation:" + installLoacation, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo4:
                int uninstallSilent = (int) executeClass("com.ningso.silence.PackageUtils", "uninstallSilent", true,
                        getApplicationContext(), "com.ningso.redenvelope", false);
                Toast.makeText(MainActivity.this, "uninstallSilent:" + uninstallSilent, Toast.LENGTH_SHORT).show();
                // Toast.makeText(MainActivity.this, "UnSilentInstall:" + PackageUtils.uninstallSilent(getApplicationContext(), "com.ningso.redenvelope", false), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_demo5:
                executeClass("com.ningso.silence.PackageUtils", "startInstalledAppDetails", true,
                        getApplicationContext(), "com.ningso.redenvelope");
                //  PackageUtils.startInstalledAppDetails(getApplicationContext(), "com.ningso.redenvelope");
                break;
            case R.id.action_demo6:
                new InStallSilent().start();
                break;
            case R.id.action_demo7:
                Boolean results7 = (Boolean) executeClass("com.ningso.silence.ShellUtils", "CopyApkSystem", true,
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
    private DexClassLoader getDexClassLoader() {
        if (classLoader == null) {
            File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + "classes.dex");
            File dexOutputDir = getDir("dex", Context.MODE_PRIVATE);
            classLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                    dexOutputDir.getAbsolutePath(), null, getClassLoader());
        }
        return classLoader;
    }

    /**
     * 加载DexloadClass里面的方法
     *
     * @param classStr   类名
     * @param methodStr  方法名
     * @param staticMeth 是否是静态方法
     * @param args       参数数组
     * @return
     */
    private Object executeClass(String classStr, String methodStr, boolean staticMeth, Object... args) {
        Class iclass;
        Object instance;
        Object result = null;
        try {
            iclass = getDexClassLoader().loadClass(classStr);
            instance = iclass.newInstance();
            if (args.length != 0) {
                Class[] params = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Context) {
                        params[i] = Context.class;
                    } else {
                        params[i] = args[i].getClass();
                    }
                }
                if (staticMeth) {
                    result = iclass.getMethod(methodStr, params).invoke(iclass, args);
                } else {
                    result = iclass.getMethod(methodStr, params).invoke(instance, args);
                }
            } else {
                if (staticMeth) {
                    result = iclass.getMethod(methodStr, new Class[]{}).invoke(iclass);
                } else {
                    result = iclass.getMethod(methodStr, new Class[]{}).invoke(instance);
                }
            }

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
        } finally {
            return result;
        }
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
            String installpath = (String) executeClass("com.ningso.silence.ShellUtils", "getInstallSilentDir", true);
            if (installpath != null) {
                installsuccuess = (int) executeClass("com.ningso.silence.PackageUtils", "installSilent", true, getApplicationContext(), installpath + "app-release.apk");
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
