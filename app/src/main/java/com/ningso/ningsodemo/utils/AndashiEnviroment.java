package com.ningso.ningsodemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by NingSo on 16/2/19.上午10:55
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class AndashiEnviroment {
    private static final String ASSETS_CMD_PATH = "cmd";
    public static int ENVIRONMENT_VERSION = 23;
    private static String cmdPath = "/data/data/com.mycheering.apps/files/cmd/";
    private static boolean inited = false;

    private AndashiEnviroment() {
    }

    public static synchronized void init(Context context) {
        synchronized (AndashiEnviroment.class) {
            if (!inited) {
                cmdPath = context.getFilesDir() + "/cmd/";
                doInit(context);
                inited = true;
            }
        }
    }

    public static void doInit(Context context) {
        File cmdDir = new File(cmdPath);
        if (!cmdDir.exists()) {
            cmdDir.mkdirs();
        }
        try {
            if (new File(cmdPath + ENVIRONMENT_VERSION).exists()) {
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            for (String file : context.getAssets().list(ASSETS_CMD_PATH)) {
                installCmd(context, file);
            }
        } catch (Throwable e2) {
        }
    }

    @SuppressLint({"NewApi"})
    private static void installCmd(Context context, String cmdFile) {
        Closeable closeable;
        Closeable closeable2;
        Throwable e;
        try {
            File targetFile = new File(cmdPath + cmdFile);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            BufferedInputStream in = new BufferedInputStream(context.getAssets().open("cmd/" + cmdFile));
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile, false));
                try {
                    byte[] buffer = new byte[4096];
                    while (true) {
                        int len = in.read(buffer);
                        if (len == -1) {
                            out.flush();
                            closeQuietly(in);
                            closeQuietly(out);
                            targetFile.setReadable(true, false);
                            targetFile.setWritable(true, false);
                            targetFile.setExecutable(true, false);
                            closeable = out;
                            closeable2 = in;
                            return;
                        }
                        out.write(buffer, 0, len);
                    }
                } catch (Throwable th) {
                    e = th;
                    closeable = out;
                    closeable2 = in;
                }
            } catch (Throwable th2) {
                e = th2;
                closeable2 = in;
            }
        } catch (Throwable th3) {
            e = th3;
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static String getCmdPath() {
        return cmdPath;
    }
}
