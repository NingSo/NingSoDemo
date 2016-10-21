package com.ningso.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ningso.shortcutbadger.Badger;
import com.ningso.shortcutbadger.ShortcutBadgeException;
import com.ningso.shortcutbadger.util.BroadcastHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by NingSo on 2016/10/15.上午10:15
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */

public class VivoHomeBadger implements Badger {

    private static final String INTENT_ACTION = "launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM";
    private static final String INTENT_EXTRA_BADGE_COUNT = "notificationNum";
    private static final String INTENT_EXTRA_PACKAGENAME = "packageName";
    private static final String INTENT_EXTRA_CLASS_NAME = "className";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        if (badgeCount <= 0) {
            badgeCount = -1;
        }
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, context.getPackageName());
        intent.putExtra(INTENT_EXTRA_CLASS_NAME, componentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        if (BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return new ArrayList<>(0);
    }
}
