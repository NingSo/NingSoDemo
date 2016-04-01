package com.ningso.ningsodemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sevenheaven.pulsecheck.PulseChecker;

public class MainActivity extends AppCompatActivity {

    private static final int HAS_ROOT_SUCCESS = 0x0001;
    private static final int HAS_ROOT_FAIL = 0x0002;
    private static final int HAS_INSTALL_SUCCESS = 0x0003;
    private static final int HAS_INSTALL_FAIL = 0x0004;

    static {
        System.loadLibrary("datagetter");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //addShortCut(MainActivity.this);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                PulseChecker.makePulse(MainActivity.this, "com.xinmei365.font/com.xinmei365.font.ui.activity.SplashActivity",
                        PulseChecker.CPNT_TYPE_ACTIVITY, 10);
                startActivity(new Intent(MainActivity.this, NewAppWidgetConfigureActivity.class));
            }
        });
    }

    private final static String SHORTCUT_ADD_ACTION =
            "com.android.launcher.action.INSTALL_SHORTCUT";
    private final static String SHORTCUT_DEL_ACTION =
            "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private final static String READ_SETTINGS_PERMISSION =
            "com.android.launcher.permission.READ_SETTINGS";

    public static void addShortCut(Context context) {
        addShortCut(context, "ningso", R.mipmap.ic_launcher);
    }

    public static void addShortCut(Context context, String shortCutName, int resourceId) {

        Intent shortCutIntent = new Intent();
        shortCutIntent.setAction(SHORTCUT_ADD_ACTION);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        shortCutIntent.putExtra("duplicate", false);

        Intent targetIntent = getTargetIntent();
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);

        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, resourceId);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        context.sendBroadcast(shortCutIntent);
    }

    private static Intent getTargetIntent() {
        Intent targetIntent = new Intent(Intent.ACTION_MAIN);
        targetIntent.setClass(App.getInstance(), MessageActivity.class);
        return targetIntent;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_demo1:
                break;
            case R.id.action_demo2:

                break;
            case R.id.action_demo3:

                break;
            case R.id.action_demo4:

                break;
            case R.id.action_demo5:

                break;
            case R.id.action_demo6:

                break;
            case R.id.action_demo7:

                break;
            case R.id.action_demo8:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
    }
}
