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
 * @author Leo Lin
 */
public class NewHtcHomeBadger implements Badger {

    private static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
    private static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
    private static final String PACKAGENAME = "packagename";
    private static final String COUNT = "count";
    private static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
    private static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {

        Intent intent1 = new Intent(INTENT_SET_NOTIFICATION);
        intent1.putExtra(EXTRA_COMPONENT, componentName.flattenToShortString());
        intent1.putExtra(EXTRA_COUNT, badgeCount);

        Intent intent = new Intent(INTENT_UPDATE_SHORTCUT);
        intent.putExtra(PACKAGENAME, componentName.getPackageName());
        intent.putExtra(COUNT, badgeCount);

        if (BroadcastHelper.canResolveBroadcast(context, intent1) || BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent1);
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.htc.launcher");
    }
}
