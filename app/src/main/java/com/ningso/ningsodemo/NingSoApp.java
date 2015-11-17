package com.ningso.ningsodemo;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;

/**
 * Created by NingSo on 15/11/10.上午11:57
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class NingSoApp extends Application {
    private static NingSoApp mInstance;

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //这里必须在super.onCreate方法之后，顺序不能变
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
    }

    public static NingSoApp getInstance() {
        return mInstance;
    }

}
