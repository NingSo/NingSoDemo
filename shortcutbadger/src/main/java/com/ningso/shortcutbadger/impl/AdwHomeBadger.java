package com.ningso.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ningso.shortcutbadger.Badger;
import com.ningso.shortcutbadger.ShortcutBadgeException;
import com.ningso.shortcutbadger.util.BroadcastHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Gernot Pansy
 */
public class AdwHomeBadger implements Badger {

    private static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
    private static final String PACKAGENAME = "PNAME";
    private static final String CLASSNAME = "CNAME";
    private static final String COUNT = "COUNT";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, componentName.getPackageName());
        intent.putExtra(CLASSNAME, componentName.getClassName());
        intent.putExtra(COUNT, badgeCount);
        if (BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "org.adw.launcher",
                "org.adwfreak.launcher"
        );
    }
}
