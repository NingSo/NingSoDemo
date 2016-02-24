package com.ningso.jni;

/**
 * Created by NingSo on 16/2/23.下午10:28
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class HttpUtils {
    public static native String getAppData(String[] keys, String[] values);

    public static native String postAppData(String[] keys, String[] values);

    public static native String postLog(String[] keys, String[] values);

    public static native String postActive(String[] keys, String[] values);

    public static native String getData(String path, String[] keys, String[] values);

    public static native String postData(String path, String[] keys, String[] values);
}
