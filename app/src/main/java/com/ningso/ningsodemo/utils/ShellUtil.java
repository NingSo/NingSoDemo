package com.ningso.ningsodemo.utils;

import android.support.design.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by NingSo on 16/2/19.上午10:47
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class ShellUtil {
    private static final int CMD_TIMEOUT = 5000;
    private static final String CMD_TIMEOUT_RESULT = "TIMEOUT";
    private static int _hasRooted;
    private static final String[] bt_list = new String[]{"/system/xbin/btscreen", "/system/etc/EngineX/btscreen", "/system/bin/btscreen"};
    private static String bt_path;
    private static final String[] su_list = new String[]{"/system/etc/EngineX/su", "/system/xbin/su", "/system/bin/su", "/system/etc/EngineX/sumoveso", "/system/xbin/sumoveso", "/system/bin/sumoveso"};
    private static String su_path;
    private static Boolean supportMount = null;
    private static String system_dev = BuildConfig.FLAVOR;
    public static final String LINE_SEPARATOR_UNIX = "\n";

    private static class Worker extends Thread {
        private Integer exit;
        private final Process process;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                this.exit = Integer.valueOf(this.process.waitFor());
            } catch (InterruptedException e) {
            }
        }
    }

    private static native long getCode();

    static {
        _hasRooted = -1;
        System.loadLibrary("andashi");
        _hasRooted = -1;
    }

    private ShellUtil() {
    }

    private static Process getRootProcess(String cmd) throws IOException {
        Process rootProcess = Runtime.getRuntime().exec(su_path == null ? "su - 0" : su_path);
        DataOutputStream rootOutStream = new DataOutputStream(rootProcess.getOutputStream());
        String[] temp = cmd.split(" ");
        String cmdPath = AndashiEnviroment.getCmdPath();
        for (String str : temp) {
            if (new File(new StringBuilder(String.valueOf(cmdPath)).append(str).toString()).exists()) {
                cmd = cmd.replace(str, new StringBuilder(String.valueOf(cmdPath)).append(str).toString());
            }
        }
        rootOutStream.write(new StringBuilder(String.valueOf(cmd)).append(LINE_SEPARATOR_UNIX).toString().getBytes());
        rootOutStream.writeBytes("exit\n");
        rootOutStream.flush();
        return rootProcess;
    }

    private static Process getBtscreenProcess(String cmd) throws IOException {
        Process rootProcess = Runtime.getRuntime().exec(new StringBuilder(String.valueOf(bt_path == null ? "btscreen" : bt_path)).append(" - ").append(getCode()).toString());
        DataOutputStream rootOutStream = new DataOutputStream(rootProcess.getOutputStream());
        String[] temp = cmd.split(" ");
        String cmdPath = AndashiEnviroment.getCmdPath();
        for (String str : temp) {
            if (new File(new StringBuilder(String.valueOf(cmdPath)).append(str).toString()).exists()) {
                cmd = cmd.replace(str, new StringBuilder(String.valueOf(cmdPath)).append(str).toString());
            }
        }
        rootOutStream.write(new StringBuilder(String.valueOf(cmd)).append(LINE_SEPARATOR_UNIX).toString().getBytes());
        rootOutStream.writeBytes("exit\n");
        rootOutStream.flush();
        return rootProcess;
    }

    public static String runCmd(String cmd, boolean isInstallApps) {
        StringBuffer result = new StringBuffer();
        Process process = null;
        try {
            switch (_hasRooted) {
                case 1:
                    process = getRootProcess(cmd);
                    break;
                case 2:
                    process = getBtscreenProcess(cmd);
                    break;
                default:
                    process = Runtime.getRuntime().exec(cmd);
                    break;
            }
            InputStream in = process.getInputStream();
            if (!isInstallApps) {
                int waitCount = 0;
                while (in.available() <= 0) {
                    Thread.sleep(100);
                    waitCount++;
                    if (waitCount >= 50) {
                        return CMD_TIMEOUT_RESULT;
                    }
                }
            }
            byte[] buffer = new byte[1024];
            while (true) {
                int len = in.read(buffer);
                if (len == -1) {
                    closeProcess(process);
                    return result.toString();
                }
                result.append(new String(buffer, 0, len));
            }
        } catch (Throwable th) {

        } finally {
            closeProcess(process);
        }
        return result.toString();
    }

    public static boolean runCmdWithoutResult(String cmd) {
        Throwable th;
        boolean z = false;
        Process process = null;
        Worker worker = null;
        try {
            switch (_hasRooted) {
                case 1:
                    process = getRootProcess(cmd);
                    break;
                case 2:
                    process = getBtscreenProcess(cmd);
                    break;
                default:
                    process = Runtime.getRuntime().exec(cmd);
                    break;
            }
            Worker worker2 = new Worker(process);
            try {
                worker2.start();
                worker2.join(5000);
                if (worker2.exit != null) {
                    if (worker2.exit.intValue() == 0) {
                        z = true;
                    }
                    closeProcess(process);
                    worker = worker2;
                } else {
                    closeProcess(process);
                    worker = worker2;
                }
            } catch (Throwable th2) {
                th = th2;
                worker = worker2;
                closeProcess(process);
                throw th;
            }
        } catch (Throwable th3) {
            if (worker != null) {
                worker.interrupt();
                Thread.currentThread().interrupt();
            }
            closeProcess(process);
            return z;
        }
        return z;
    }

    public static boolean runCmdWithoutRootAndResult(String cmd) {
        Throwable th;
        boolean z = false;
        Process process = null;
        Worker worker = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            Worker worker2 = new Worker(process);
            try {
                worker2.start();
                worker2.join(5000);
                if (worker2.exit != null) {
                    if (worker2.exit.intValue() == 0) {
                        z = true;
                    }
                    closeProcess(process);
                    worker = worker2;
                } else {
                    closeProcess(process);
                    worker = worker2;
                }
            } catch (Throwable th2) {
                th = th2;
                worker = worker2;
                closeProcess(process);
                throw th;
            }
        } catch (Throwable th3) {
            if (worker != null) {
                worker.interrupt();
                Thread.currentThread().interrupt();
            }
            closeProcess(process);
            return z;
        }
        return z;
    }

    public static String runCmdWithoutRoot(String cmd) {
        StringBuffer result = new StringBuffer();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int len = in.read(buffer);
                if (len == -1) {
                    break;
                }
                result.append(new String(buffer, 0, len));
            }
        } catch (Throwable th) {
        } finally {
            closeProcess(process);
        }
        Log.e("shell", result.toString());
        return result.toString();
    }

    public static synchronized boolean hasRooted() {
        boolean z = true;
        synchronized (ShellUtil.class) {
            if (_hasRooted != -1) {
                if (_hasRooted <= 0) {
                    z = false;
                }
            } else if (!hasBtScreen()) {
                Process process = null;
                try {
                    int i;
                    int len;
                    for (String path : su_list) {
                        if (new File(path).exists()) {
                            su_path = path;
                            _hasRooted = 1;
                            break;
                        }
                    }
                    process = getRootProcess("id");
                    InputStream in = process.getInputStream();
                    byte[] buffer = new byte[64];
                    StringBuilder result = new StringBuilder();
                    while (true) {
                        len = in.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        result.append(new String(buffer, 0, len));
                    }
                    if (result.toString().contains("uid=0")) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    _hasRooted = i;
                    if (_hasRooted < 1 && (new File("/system/xbin/su").exists() || new File("/system/bin/su").exists())) {
                        su_path = "su";
                        process = getRootProcess("id");
                        in = process.getInputStream();
                        buffer = new byte[64];
                        result = new StringBuilder();
                        while (true) {
                            len = in.read(buffer);
                            if (len == -1) {
                                break;
                            }
                            result.append(new String(buffer, 0, len));
                        }
                        if (result.toString().contains("uid=0")) {
                            i = 1;
                        } else {
                            i = 0;
                        }
                        _hasRooted = i;
                    }
                    if (_hasRooted > 0) {
                        closeProcess(process);
                    } else {
                        closeProcess(process);
                        z = false;
                    }
                } catch (Throwable th) {
                    z = false;
                } finally {
                    closeProcess(process);
                }
            }
        }
        return z;
    }

    private static boolean hasBtScreen() {
        int i = 2;
        boolean z = true;
        Process process = null;
        InputStream in = null;
        try {
            byte[] buffer;
            StringBuilder result;
            int len;
            for (String path : bt_list) {
                if (new File(path).exists()) {
                    bt_path = path;
                    process = getBtscreenProcess("id");
                    in = process.getInputStream();
                    buffer = new byte[64];
                    result = new StringBuilder();
                    while (true) {
                        len = in.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        result.append(new String(buffer, 0, len));
                    }
                    _hasRooted = result.toString().contains("uid=0") ? 2 : _hasRooted;
                    if (_hasRooted == 2) {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Throwable th) {
                            }
                        }
                        closeProcess(process);
                        return true;
                    }
                    bt_path = null;
                }
            }
            process = getBtscreenProcess("id");
            in = process.getInputStream();
            buffer = new byte[64];
            result = new StringBuilder();
            while (true) {
                len = in.read(buffer);
                if (len == -1) {
                    break;
                }
                result.append(new String(buffer, 0, len));
            }
            if (!result.toString().contains("uid=0")) {
                i = _hasRooted;
            }
            _hasRooted = i;
            if (_hasRooted <= 1) {
                z = false;
            }
            try {
                in.close();
            } catch (Throwable th2) {
            }
            closeProcess(process);
            return z;
        } catch (Throwable th3) {
            return false;
        }
    }

    public static boolean supportMount() {
        if (supportMount != null) {
            return supportMount.booleanValue();
        }
        String dev = BuildConfig.FLAVOR;
        String mountInfo = runCmd("mount");
        if (!TextUtils.isEmpty(mountInfo)) {
            for (String line : mountInfo.split(LINE_SEPARATOR_UNIX)) {
                if (line.contains(" /system ")) {
                    String[] items = line.split(" ");
                    int i = 0;
                    while (i < items.length) {
                        if (items[i].equals("/system") && i > 0) {
                            dev = items[i - 1];
                        }
                        i++;
                    }
                }
            }
        }
        runCmdWithoutResult("mount -o remount,rw " + dev + " /system && echo test > /system/app/test.tmp");
        boolean wFileExist = new File("/system/app/test.tmp").exists();
        runCmdWithoutResult("rm /system/app/test.tmp && mount -o remount,ro " + dev + " /system");
        return wFileExist;
    }

    private static void closeProcess(Process process) {
        if (process != null) {
            try {
                process.destroy();
            } catch (Throwable th) {
            }
        }
    }

    public static String getSystemDev() {
        if (!TextUtils.isEmpty(system_dev)) {
            return system_dev;
        }
        String mountInfo = runCmd("mount");
        if (!TextUtils.isEmpty(mountInfo)) {
            for (String line : mountInfo.split(LINE_SEPARATOR_UNIX)) {
                if (line.contains(" /system ")) {
                    String[] items = line.split(" ");
                    int i = 0;
                    while (i < items.length) {
                        if (items[i].equals("/system") && i > 0) {
                            system_dev = items[i - 1];
                        }
                        i++;
                    }
                }
            }
        }
        return system_dev;
    }

    public static int get_hasRooted() {
        return _hasRooted;
    }

    public static String runCmd(String cmd) {
        return runCmd(cmd, false);
    }

    public static String runAndashiCmd(String cmd) {
        return runCmd(AndashiEnviroment.getCmdPath() + cmd);
    }

    public static boolean runAndashiCmdWithoutResult(String cmd) {
        return runCmdWithoutResult(AndashiEnviroment.getCmdPath() + cmd);
    }
}
