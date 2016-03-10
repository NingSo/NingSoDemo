package com.ningso.silence.utils;


import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * ShellUtils
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellUtils#checkRootPermission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellUtils#execCommand(String, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */

/**
 * Created by NingSo on 16/2/16.下午9:01
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class ShellUtils {

    private static final String COMMAND_SU = "su";
    private static final String COMMAND_SH = "sh";
    private static final String COMMAND_EXIT = "exit\n";
    private static final String COMMAND_LINE_END = "\n";
    private static String SYSTEM_APP_DIR = "/system/app/";
    private static String SYSTEM_PRIV_APP_DIR = "/system/priv-app/";

    private static final String MOUNT_1 = "mount -o remount,rw /system";
    private static final String MOUNT_2 = "mount -o remount rw /system";
    private static final String MOUNT_3 = "mount -o remount /dev/block/mtdblock0 /system";
    private static final String MOUNT_4 = "mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system";

    private static String BUSYBOXPATH = "";
    private static Process localProcess = null;
    private static DataOutputStream dos = null;
    private static DataInputStream in = null;
    private static final String CHECK_CMD_END_TEXT = "--CHECK_CMD_END--";

    private static final int kSystemRootStateUnknow = -1;
    private static final int kSystemRootStateDisable = 0;
    private static final int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    /**
     * check MobilePhone whether has root permission
     *
     * @return
     */
    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f;
        final String kSuSearchPaths[] = {
                "/system/bin/",
                "/system/xbin/",
                "/system/sbin/",
                "/sbin/",
                "/vendor/bin/"
        };
        try {
            for (String kSuSearchPath : kSuSearchPaths) {
                f = new File(kSuSearchPath + "su");
                if (f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    public ShellUtils() {

    }

    /**
     * check whether gave root permission
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command) {
        return execCommand(new String[]{command}, true, true);
    }

    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot  whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command list
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command array
     * @param isRoot   whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     *
     * @param command         command
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command list
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands        command array
     * @param isRoot          whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     * <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     * {@link CommandResult#errorMsg} is null.</li>
     * <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     * </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result,
                successMsg == null ? null : successMsg.toString(),
                errorMsg == null ? null : errorMsg.toString());
    }

    /**
     * result of command
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
     * linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {

        /**
         * result of command
         **/
        public int result;
        /**
         * success message of command result
         **/
        public String successMsg;
        /**
         * error message of command result
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    /***
     * TODO
     *
     * @param cmdStr
     * @return
     */
    public static boolean executeCmd(String cmdStr) {
        cmdStr = getSDcardPath(cmdStr);
        try {
            if (!isSuProcessRunning()) {
                if (!initSuProcess(0)) {
                    return false;
                }
            }
            String line;
            dos.writeBytes(cmdStr + "\n");
            dos.flush();
            dos.writeBytes("echo magic-text $? \n");
            dos.flush();
            long waittimeout = System.currentTimeMillis() + 1000 * 5;
            while (System.currentTimeMillis() < waittimeout) {
                while (in.available() > 0 && (line = in.readLine()) != null) {
                    if (line.contains("permission denied")
                            || line.contains("operation not permitted")
                            || line.contains("connect ui: timer expired")
                            || line.contains("not found")
                            || line.contains("no such tool")) { // 授权失败
                        return false;
                    }
                    if (line.contains("magic-text")) {
                        String[] str = line.split(" ");
                        if (str.length > 1 && "0".equals(str[1])) {
                            return true;
                        }
                        DataInputStream errorin = new DataInputStream(localProcess.getErrorStream());
                        String error;
                        if ((!"0".equals(str[1])) && ((error = errorin.readLine()) == null || "".equals(error))) {
                            return true;
                        }
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static boolean initSuProcess(long timeout) {
        boolean isOk = true;
        String cmd = "su";
        try {
            releaseSuProcess();
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            localProcess = builder.start();
            dos = new DataOutputStream(localProcess.getOutputStream());
            in = new DataInputStream(localProcess.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isOk = false;
        }
        return isOk;
    }

    /**
     * 判断全局的process时候还在
     *
     * @return
     */
    private static boolean isSuProcessRunning() {
        if (localProcess == null) {
            return false;
        }
        boolean isRunning = false;
        try {
            localProcess.exitValue();
        } catch (Exception e) {
            isRunning = true;
        }
        if (!isRunning) {
            return false;
        }

        boolean isPermitted = false;
        try {
            // 检测是否还有授权 -- Start
            dos.writeBytes("echo " + CHECK_CMD_END_TEXT + "\n");
            dos.flush();
            String line;
            boolean isFinish = false;
            long waitTimeout = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < waitTimeout) {
                while (in.available() > 0
                        && (line = in.readLine()) != null) {
                    if (isPermissionDenied(line)) { // 授权失败
                        isPermitted = false;
                        isFinish = true;
                    } else if (line.contains(CHECK_CMD_END_TEXT)) { // 授权成功
                        isPermitted = true;
                        isFinish = true;
                    }
                    if (isFinish) {
                        break;
                    }
                }
                if (isFinish) {
                    break;
                }
            }

            if (!isPermitted) {
                releaseSuProcess();
            }
            // 检测是否还有授权 -- End
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isPermitted;
    }


    /**
     * 释放全局的process
     */
    public static synchronized void releaseSuProcess() {
        if (dos != null) {
            try {
                dos.writeBytes("exit\n");
                dos.flush();
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dos = null;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
        }
        if (localProcess != null) {
            try {
                localProcess.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            localProcess = null;
        }
    }


    private static boolean isPermissionDenied(String output) {
        String str = output.toLowerCase();
        return str.contains("permission denied") || str.contains("operation not permitted")
                || str.contains("connect ui: timer expired") || str.contains("can't set uid 0")
                || str.contains("can't set gid 0") || str.contains("no such tool");
    }

    public static String getSDcardPath(String str) {
        if (Build.VERSION.SDK_INT >= 18) {
            return str.replaceAll(Environment.getExternalStorageDirectory()
                    .getPath(), "/mnt/sdcard");
        } else {
            return str;
        }
    }

    /**
     * @param srcfont_path
     * @return
     */
    public static boolean CopyApkToSystem(String srcfont_path) {
        if (!remount()) {
            return false;
        }
        if (srcfont_path == null) {
            return false;
        }
        String tempfile;
        String apkName = srcfont_path.substring(srcfont_path.lastIndexOf('/') + 1);
        if (apkName.contains(".apk") && !apkName.endsWith("apk")) {
            //哎,遇到不明程序执行安装操作原文件名被修改问题,暂时这么处理
            apkName = apkName.substring(0, apkName.lastIndexOf(".")) + ".apk";
            Log.d("plugin", "copy " + apkName);
        }
        boolean hasPrivApp = chechFile();
        if (hasPrivApp) {
            tempfile = SYSTEM_PRIV_APP_DIR + apkName;
        } else {
            tempfile = SYSTEM_APP_DIR + apkName;
        }
        Log.d("plugin", tempfile);
        String cmdStr = "cp -f " + srcfont_path + " " + tempfile;
        if (!executeCmd(cmdStr)) {
            cmdStr = "cat " + srcfont_path + " > " + tempfile;
            if (!executeCmd(cmdStr)) {
                cmdStr = "dd if=" + srcfont_path + " of=" + tempfile;
                if (!executeCmd(cmdStr)) {
                    cmdStr = "busybox cp -f " + srcfont_path + " " + tempfile;
                    if (!executeCmd(cmdStr)) {
                        try {
                            copyFile(srcfont_path, tempfile);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("plugin", "copy fail &remove tempfile");
                            remove(tempfile);
                            return false;
                        }
                    }
                }
            }
        }
        if (hasPrivApp) {
            cmdStr = "chmod 755 " + tempfile;
        } else {
            cmdStr = "chmod 644 " + tempfile;
        }
        if (!executeCmd(cmdStr)) {
            remove(tempfile);
            return false;
        }
        return true;
    }

    private static boolean rename(String srcFile, String oldname, String newname) {
        if (checkFile(srcFile, oldname)) {
            String cmdStr = "rename " + oldname + " " + newname;
            if (!executeCmd(cmdStr)) {
                cmdStr = "mv " + oldname + " " + newname;
                if (!executeCmd(cmdStr)) {
                    ////
                    Log.e("E", "e");
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private static boolean checkFile(String srcFile, String desFile) {
        File src = new File(srcFile);
        File des = new File(desFile);
        if (!(src.exists() && des.exists())) {
            return false;
        }
        return src.length() == des.length();
    }

    private static boolean chechFile() {
        File src = new File(SYSTEM_PRIV_APP_DIR);
        if (src.exists() || src.isDirectory()) {
            return true;
        }
        return false;
    }

    /**
     * 获取安装路径
     *
     * @return
     */
    public static String getInstallSilentDir() {
        return chechFile() ? SYSTEM_PRIV_APP_DIR : SYSTEM_APP_DIR;
    }

    /**
     * Check whether the disk read/write
     *
     * @return
     */
    public static boolean remount() {
        if (!executeCmd(BUSYBOXPATH + " " + MOUNT_2)) {
            if (!executeCmd(MOUNT_1)) {
                if (!executeCmd(MOUNT_2)) {
                    if (!executeCmd(MOUNT_3)) {
                        if (!executeCmd(MOUNT_4)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 移除文件
     *
     * @param path
     * @return
     */
    private static boolean remove(String path) {
        // if(!remount())
        // return false;
        String cmdStr = BUSYBOXPATH + " rm -r " + path;
        if (!executeCmd(cmdStr)) {
            cmdStr = "rm -r " + path;
            executeCmd(cmdStr);
        }
        return true;
    }


    /**
     * @param srcFile
     * @param desFile
     * @throws Exception
     */
    public static void copyFile(String srcFile, String desFile) throws Exception {
        if (srcFile != null && srcFile.trim().length() > 0) {
            File src = new File(srcFile);
            if (!src.exists()) {
                throw new Exception("src file is not exists");
            }
            FileInputStream fis = null;
            FileOutputStream fos = null;
            boolean isEnough = getAvailableExternalMemorySize() > src.length();
            if (isEnough) { // 内存足够
                try {
                    File des = new File(desFile);
                    if (!des.exists()) {
                        des.createNewFile();
                    }
                    fis = new FileInputStream(srcFile);
                    fos = new FileOutputStream(desFile);

                    byte[] buffer = new byte[1024 * 100];
                    int c = -1;
                    while ((c = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, c);
                    }
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    File file = new File(desFile);
                    if (file.exists()) {
                        file.deleteOnExit();
                    }
                    throw new Exception("IO Exception");
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                throw new Exception("memory not enough");
            }
        }
    }

    public static void copyLibSo2SystemLib(String dirfile) {
        try {
            if (dirfile == null) {
                return;
            }
            File file = new File(dirfile);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length > 0) {
                    for (File child : files) {
                        CopyFile(child);
                    }
                }
                if (file.listFiles().length == 0) {
                    CopyFile(file);
                }
            } else if (file.isFile()) {
                CopyFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean CopyFile(File file) {
        String srcfont_path;
        String tempfile;
        if (!file.exists() && !file.isFile()) {
            return false;
        }
        srcfont_path = file.getAbsolutePath();
        tempfile = "system/lib/" + file.getName();
        String cmdStr = "cp -f " + srcfont_path + " " + tempfile;
        if (!executeCmd(cmdStr)) {
            cmdStr = "cat " + srcfont_path + " > " + tempfile;
            if (!executeCmd(cmdStr)) {
                cmdStr = "dd if=" + srcfont_path + " of=" + tempfile;
                if (!executeCmd(cmdStr)) {
                    cmdStr = "busybox cp -f " + srcfont_path + " " + tempfile;
                    if (!executeCmd(cmdStr)) {
                        try {
                            copyFile(srcfont_path, tempfile);
                        } catch (Exception e) {
                            e.printStackTrace();
                            remove(tempfile);
                            return false;
                        }
                    }
                }
            }
        }
        cmdStr = "chmod 644 " + tempfile;
        Log.e("ShellUtils", "src: " + srcfont_path + "\ntarget: " + tempfile + "\n----" + executeCmd(cmdStr) + "-----\n");
        return executeCmd(cmdStr);
    }

    /**
     * Sdcard is available
     *
     * @return flag
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageDirectory().canWrite();
    }

    /**
     * Return sdcard size are available, and the unit is byte
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * @param file
     */
    public void deleteDir(File file) {
        try {
            if (file == null) {
                return;
            }
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length > 0) {
                    for (File child : files) {
                        deleteDir(child);
                    }
                }
                if (file.listFiles().length == 0) {
                    file.delete();
                }
            } else if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
