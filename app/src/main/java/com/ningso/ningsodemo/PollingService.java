package com.ningso.ningsodemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by NingSo on 16/2/25.下午4:17
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class PollingService extends Service {
    public static final String ACTION = "com.ryantang.service.PollingService.Action";

    private NotificationCompat.Builder notifyBuilder;
    private Notification mNotification;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initNotifiManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PollingThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    //初始化通知栏配置
    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_child_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_child_white_24dp))
                .setContentText("New Message")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setWhen(System.currentTimeMillis());
        mNotification = notifyBuilder.build();
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    //弹出Notification
    private void showNotification() {
        mNotification.when = System.currentTimeMillis();
        //Navigator to the new activity when click the notification title
        Intent intent = new Intent(this, MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notifyBuilder.setContentText("You have new message!");
        notifyBuilder.setContentIntent(pendingIntent);
        mManager.notify(0, mNotification);
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            Log.e("PollingService", "Polling :");
            count++;
            //当计数能被5整除时弹出通知
            if (count % 5 == 0) {
                showNotification();
                Log.e("PollingService", "New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("PollingService", "onDestroy :");
    }
}
