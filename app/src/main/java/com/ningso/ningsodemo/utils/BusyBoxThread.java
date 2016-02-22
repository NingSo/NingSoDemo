package com.ningso.ningsodemo.utils;

import android.content.Context;

/**
 * Created by NingSo on 16/2/17.下午5:05
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class BusyBoxThread extends Thread {

    Context ctx;

    public BusyBoxThread(Context ctx) {
        this.ctx = ctx;
    }

    public void run() {
//        try {
//            ShellUtils.BUSYBOXPATH = this.ctx.getFilesDir().getAbsolutePath() + "/busybox";
//            File file = new File(ShellUtils.BUSYBOXPATH);
//            if (file.exists()) {
//                return;
//            }
//            ShellUtils.saveIncludedFileIntoFilesFolder(R.raw.busybox, "busybox", this.ctx);
//            Thread.sleep(4000);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

    }
}