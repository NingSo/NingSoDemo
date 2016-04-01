package com.sevenheaven.pulsecheck;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by 7heaven on 15/4/10.
 */
public class PulseChecker {

    public static final int CPNT_TYPE_SERVICE = 0;
    public static final int CPNT_TYPE_ACTIVITY = 1;
    public static final int CPNT_TYPE_BROADCAST = 2;
    public static final int CPNT_TYPE_ACTION_SERVICE = 3;
    public static final int CPNT_YYPE_ACTION_ACTIVITY = 4;

    private static boolean libraryLoaded = false;

    static {
        try {
            System.loadLibrary("pulse");
            libraryLoaded = true;
        } catch (Throwable e) {
            libraryLoaded = false;
            e.printStackTrace();
        }
    }

    public static void makePulse(Context context, String componentNameOrAction, int cpntType, int pulseGap) {

        if (!loadAbsoluteLibrary(context)) return;

        String pid = android.os.Process.myPid() + "";

      //  if (checkFile(pid)) return;

        if (Build.VERSION.SDK_INT < 17) {
            pulse(pid, null, componentNameOrAction, cpntType, pulseGap);
        } else {
            pulse(pid, getUserSerial(context), componentNameOrAction, cpntType, pulseGap);
        }
    }

    private static String getUserSerial(Context context) {

        Object userManager = context.getSystemService(Context.USER_SERVICE);
        if (userManager == null) {
            return null;
        }

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);

            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);

            return String.valueOf(userSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean loadAbsoluteLibrary(Context context) {
        if (libraryLoaded) {
            return true;
        } else {
            try {
                System.load("/data/data/" + context.getPackageName() + "/lib/libpulse.so");
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static boolean checkFile(String myPid) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/pulse_checker");
        if (file.exists() && file.isDirectory()) {
            String[] files = file.list();

            if (files != null && files.length > 0) {
                return true;
            }

//            StringBuilder sb = new StringBuilder();
//
//            try{
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                String line;
//                while ((line = br.readLine()) != null) {
//                    sb.append(line);
//                    sb.append('\n');
//                }
//                br.close();
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//
//            if(sb.length() > 0){
//                Log.d("readed file:" + System.currentTimeMillis(), sb.toString());
//                return true;
//            }else{
//                Log.d("read file failed", "failed");
//            }
        }

        return false;
    }

    private static native int pulse(String pid, String userSerial, String componentName, int cpntType, int pulseGap);
}
