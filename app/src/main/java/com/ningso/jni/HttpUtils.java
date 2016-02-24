package com.ningso.jni;

import android.content.Context;

import java.util.Map;

/**
 * Created by NingSo on 16/2/23.下午10:28
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class HttpUtils {

    private static boolean libraryLoaded = false;

    static {
        try {
            System.loadLibrary("datagetter");
            libraryLoaded = true;
        } catch (Throwable e) {
            libraryLoaded = false;
            e.printStackTrace();
        }
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

    /**
     * get
     *
     * @param str_Api
     * @param params
     * @return
     */
    public static String AsycGetData(String str_Api, Map<String, String> params) {
        String[] keys = new String[params.size()];
        String[] values = new String[params.size()];
        params.size();
        int count = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            keys[count] = entry.getKey();
            values[count++] = entry.getValue();
        }
        return getData(str_Api, keys, values);
    }


    /**
     * post
     *
     * @param str_Api
     * @param params
     * @return
     */
    public static String AsycPostData(String str_Api, Map<String, String> params) {
        String[] keys = new String[params.size()];
        String[] values = new String[params.size()];
        params.size();
        int count = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            keys[count] = entry.getKey();
            values[count++] = entry.getValue();
        }
        return postData(str_Api, keys, values);
    }

    private static native String getAppData(String[] keys, String[] values);

    private static native String postAppData(String[] keys, String[] values);

    private static native String postLog(String[] keys, String[] values);

    private static native String postActive(String[] keys, String[] values);

    private static native String getData(String path, String[] keys, String[] values);

    private static native String postData(String path, String[] keys, String[] values);
}
