package com.ningso.ningsodemo.downloadmanager.bizs;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.ningso.ningsodemo.NingSoApp;
import com.ningso.ningsodemo.downloadmanager.interfaces.DLTaskListener;
import com.ningso.ningsodemo.utils.ILog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author ZhaoKaiQiang 用于下载应用内推荐APK
 *         2015-08-06
 */
public class DLService extends Service {

    private onDownloadListener mDownloadListener;
    private ArrayList<String> downloadingURLs;
    public HashMap<String, Integer> idMap;

    @Override
    public void onCreate() {
        super.onCreate();
        downloadingURLs = new ArrayList<>();
        idMap = new HashMap<>();
    }

    public void stopDownload(String url) {
        DLManager.getInstance(NingSoApp.getInstance()).stop(url);
    }

    public void startDownload(String url, String path, String apkName, final int id, int iconId) {

        final NotificationManager nm = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(apkName).setSmallIcon(iconId);

        DLManager.getInstance(NingSoApp.getInstance()).start(url, path, apkName, new DLTaskListener() {

            @Override
            public void onProgress(int progress, String url) {
                builder.setProgress(100, progress, false);
                nm.notify(id, builder.build());
                if (null != mDownloadListener) mDownloadListener.onProgress(progress, url);
            }

            @Override
            public void onStart(String fileName, String url) {
                super.onStart(fileName, url);
                if (null != mDownloadListener) mDownloadListener.onStart(fileName, url);
                downloadingURLs.add(url);
                ILog.d("onStart , downloading URLs length = " + downloadingURLs.size());
            }

            @Override
            public void onError(String error, String url) {
                super.onError(error, url);
                if (null != mDownloadListener) mDownloadListener.onError(error, url);
            }

            @Override
            public void onFinish(File file, String url) {
                if (null != mDownloadListener) mDownloadListener.onFinish(file, url);
                nm.cancel(id);

                installAPK(DLService.this, file);

                downloadingURLs.remove(url);
                ILog.d("onFinish downloadingURLs length = " + downloadingURLs.size());
                if (downloadingURLs.size() == 0) {
                    ILog.d("stopSelf() downloadingURLs length = " + downloadingURLs.size());
                    stopSelf();
                }

            }

            @Override
            public void onStop(String url) {
                super.onStop(url);
                if (null != mDownloadListener) mDownloadListener.onStop(url);
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    public class DownloadBinder extends Binder {

        public DLService getService() {
            return DLService.this;
        }

    }

    public void setOnDownloadListener(onDownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    public static void installAPK(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public interface onDownloadListener {

        void onProgress(int progress, String url);

        void onStart(String fileName, String url);

        void onError(String error, String url);

        void onFinish(File file, String url);

        void onStop(String url);

    }

}
