package com.ningso.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ningso.shortcutbadger.Badger;
import com.ningso.shortcutbadger.ShortcutBadgeException;

import java.util.Arrays;
import java.util.List;


/**
 * @author Jason Ling
 */
public class HuaweiHomeBadger implements Badger {

    private static final String LOG_TAG = HuaweiHomeBadger.class.getSimpleName();

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        String launcherClassName = componentName.getClassName();
        if (launcherClassName == null) {
            Log.d(LOG_TAG, "Main activity is null");
            return;
        }
        checkIsSupportedByVersion(context);

        if (isSupportedBade) {
            Bundle extra = new Bundle();
            extra.putString("package", context.getPackageName());
            extra.putString("class", launcherClassName);
            extra.putInt("badgenumber", badgeCount);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_launcher_badge", null, extra);
        }

        Bundle localBundle = new Bundle();
        localBundle.putString("package", context.getPackageName());
        localBundle.putString("class", launcherClassName);
        localBundle.putInt("badgenumber", badgeCount);
        context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);


    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.huawei.android.launcher"
        );
    }

    boolean isSupportedBade = false;

    public void checkIsSupportedByVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher", 0);
            if (info.versionCode >= 63029) {
                isSupportedBade = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
