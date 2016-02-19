package com.ningso.ningsodemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.PrintWriter;

public class ApkController {
    public static boolean install(String apkPath, Context context) {
        // 先判断手机是否有root权限
        if (hasRootPerssion()) {
            // 有root权限，利用静默安装实现
            return clientInstall(apkPath);
        } else {
            // 没有root权限，利用意图进行安装
            File file = new File(apkPath);
            if (!file.exists())
                return false;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        }
    }

    /**
     * 描述: 卸载
     */
    public static boolean uninstall(String packageName, Context context) {
        if (hasRootPerssion()) {
            // 有root权限，利用静默卸载实现
            return clientUninstall(packageName);
        } else {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
            return true;
        }
    }

    /**
     * 判断手机是否有root权限
     */
    private static boolean hasRootPerssion() {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默安装
     * $ adb push /Users/NingSo/Github/NingSoDemo/app/build/outputs/apk/app-debug.apk /data/local/tmp/com.ningso.ningsodemo
     * $ adb shell pm install -r "/data/local/tmp/com.ningso.ningsodemo"
     */
    private static boolean clientInstall(String apkPath) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 " + apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r " + apkPath);
//			PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默卸载
     */
    private static boolean clientUninstall(String packageName) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall " + packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 启动app
     */
    public static boolean startApp(String packageName, String activityName) {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " -a " + "android.intent.action.MAIN -c android.intent.category.LAUNCHER";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isSuccess;
    }


    /**
     * 0->成功
     * 1->失败
     * 其他未知情况
     *
     * @param value
     * @return
     */
    private static boolean returnResult(int value) {
        if (value == 0) {
            return true;
        } else if (value == 1) {
            return false;
        } else {
            return false;
        }
    }
}
