package com.ningso.ningsodemo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;

import com.ningso.ningsodemo.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by NingSo on 16/4/17.下午6:21
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */

public class WaitingSpinner {
    private static ProgressListener mProgressListener;
    private static AtomicBoolean sBlock = new AtomicBoolean(false);
    private static boolean sCanceled;
    private static ProgressDialog sPd;

    public interface ProgressListener {
        void onProgressChanged(double d);
    }

    private static class Listener implements ProgressListener {
        private Listener() {
        }

        public void onProgressChanged(double persent) {
            if (WaitingSpinner.sPd != null) {
                WaitingSpinner.sPd.setProgress((int) (100.0d * persent));
            }
        }
    }

    public static void show(Context cx) {
        if (cx != null) {
            show(cx, false, false);
        }
    }

    public static void show(Context cx, boolean cancelable) {
        if (cx != null) {
            show(cx, false, cancelable);
        }
    }

    public static void showCancelable(Context cx) {
        if (cx != null) {
            show(cx, false, true);
        }
    }

    public static boolean isCanceled() {
        return sCanceled;
    }

    public static void show(Context cx, boolean horizontal, boolean cancelable) {
        if (!sBlock.getAndSet(true)) {
            sPd = new ProgressDialog(cx, R.style.BlankPBTheme);
            sPd.getWindow().setGravity(Gravity.CENTER);
            sPd.setIndeterminateDrawable(cx.getResources().getDrawable(R.drawable.loading));
            if (horizontal) {
                sPd.setProgressStyle(1);
                sPd.setProgressDrawable(cx.getResources().getDrawable(R.drawable.progress_bar_states));
                sPd.setMax(100);
            }
            sPd.setCancelable(cancelable);
            sPd.show();
            sCanceled = false;
            sPd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    WaitingSpinner.sCanceled = true;
                    WaitingSpinner.dismiss();
                }
            });
        }
    }

    public static boolean isBlocking() {
        return sBlock.get();
    }

    public static void dismiss() {
        mProgressListener = null;
        sBlock.set(false);
        try {
            if (sPd != null) {
                sPd.dismiss();
                sPd = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProgressListener getProgressListener() {
        if (mProgressListener == null) {
            mProgressListener = new Listener();
        }
        return mProgressListener;
    }
}