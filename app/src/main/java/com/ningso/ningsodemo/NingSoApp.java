package com.ningso.ningsodemo;

import android.app.Application;

/**
 * Created by NingSo on 15/11/10.上午11:57
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class NingSoApp extends Application {
    private static NingSoApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static NingSoApp getInstance() {
        return mInstance;
    }

}
