package com.ningso.silence.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/**
 * Created by NingSo on 16/3/1.下午5:15
 * 优先级比较低，为后台线程级别，并且是串行处理。用来处理一些不重要并且实时性不高的任务，比如发送错误日志.
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class BackgroundHandler {

    private static Handler handler = null;

    public static void post(Runnable r) {
        post(r, 0);
    }

    public static void post(final Runnable r, final long delay) {
        if (handler == null) {
            HandlerThread thread = new HandlerThread("Background Thread", Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            handler = new Handler(thread.getLooper());
        }
        handler.postDelayed(r, delay);
    }

    public static void remove(final Runnable r) {
        if (handler != null && r != null) {
            handler.removeCallbacks(r);
        }
    }

}