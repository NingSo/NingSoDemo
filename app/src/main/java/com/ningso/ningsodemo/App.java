package com.ningso.ningsodemo;

import android.app.Application;

/**
 * Created by NingSo on 16/2/23.下午4:55
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class App extends Application {
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static App getInstance() {
        return mInstance;
    }
}
