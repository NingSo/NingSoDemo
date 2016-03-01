//package com.ningso.ningsodemo;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//import com.qisi.datacollect.config.*;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.qisi.datacollect.ABTest.ABTest;
//import com.qisi.datacollect.FileInfo.FileInfo;
//import com.qisi.datacollect.sdk.Agent;
//import com.qisi.datacollect.sdk.object.AgentData;
//import com.qisi.datacollect.sdk.common.CommonUtil;
//import com.qisi.datacollect.sdk.common.DataConstants;
//import com.qisi.datacollect.sdk.common.TypeConstants;
//import com.qisi.datacollect.service.AgentService;
//import com.qisi.datacollect.sdk.common.AgentConstants;
//import com.qisi.datacollect.util.HttpUtil;
//import com.qisi.datacollect.util.Log;
//import com.qisi.datacollect.util.MD5Util;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.SystemClock;
//
//import javax.print.DocFlavor;
//
//public class AgentReceiver extends BroadcastReceiver {
//  public static final String md5 = MD5Util.md5("koala" + new Random(System.currentTimeMillis()).nextInt());
//  public static final String ALARM_ACTION_HEARTBEAT = md5 + "heartbeat_alarm";
//  public static final String ALARM_ACTION_FETCH_CONFIG = md5 + "fetch_config_alarm";
//  public static final String ALARM_ACTION_EVENT = md5 + "event_alarm";
//  public static final String ALARM_ACTION_WORD = md5 + "word_alarm";
//  public static final String ALARM_ACTION_FORCE_FETCH_CONFIG = md5 + "force_fetch_config_alarm";
//
//  public static final long FETCH_CONFIG_INTERVAL = AlarmManager.INTERVAL_DAY;
//  public static final long FETCH_CONFIG_INTERVAL_DEBUG = 5 * 60 * 1000;// AlarmManager.INTERVAL_FIFTEEN_MINUTES;
//  public static boolean fetch_config_get_success = false;
//
//  public static final String APP_LISTENER = "app_listener";
//  public static final String APP_INSTALL = "install";
//  public static final String APP_UPDATE = "update";
//  public static final String APP_UNINSTALL = "uninstall";
//  public static final String APP_PACKAGE = "pkg";
//
//  public static final String ALARM_ACTION_ABTEST = "abtest_alarm";
//
//
//  private Context mContext;
//  // 重启alarm 标志
//  public static boolean isrestartevent = true;
//  public static boolean isrestartword = true;
//  public static boolean isrestarthb = true;
//  public static boolean isrestartab = true;
//
//
//  // config 获取失败后间隔一段时间后才允许再次获取
//  private static long last_get_config_time = 0l;
//  private static long last_try_send_fail_files = 0l;
//
//  private String fetch_config_level_normal = "normal";
//  private String fetch_config_level_force = "force";
//
//  @Override
//  public void onReceive(Context context, Intent intent) {
//    mContext = context;
//
//    String action = intent.getAction();
////    CommonUtil.printLog("onReceive", action);
//
//    if (ALARM_ACTION_EVENT.equals(action)) {
//      AgentService.postFromFile(mContext, TypeConstants.OPERATE_TYPE, AgentService.POSTCMD_SOURCE_ALARM);
//    } else if (ALARM_ACTION_FETCH_CONFIG.equals(action)) {
//      new Thread(new Runnable() {
//        public void run() {
//          getConfigFromServer(fetch_config_level_normal);
//        }
//      }).start();
//    } else if (ALARM_ACTION_FORCE_FETCH_CONFIG.equals(action)) {
//      new Thread(new Runnable() {
//        public void run() {
//          getConfigFromServer(fetch_config_level_force);
//        }
//      }).start();
//    } else if (ALARM_ACTION_ABTEST.equals(action)) {
//      new Thread(new Runnable() {
//        public void run() {
//          ABTest.sendABTest();
//        }
//      }).start();
//    } else if (ALARM_ACTION_HEARTBEAT.equals(action)) {
//      new Thread(new Runnable() {
//        public void run() {
//          doHeartbeat();
//        }
//      }).start();
//    } else if (ALARM_ACTION_WORD.equals(action)) {
//      AgentService.postFromFile(mContext, TypeConstants.WORD_TYPE, AgentService.POSTCMD_SOURCE_ALARM);
//    } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)
//            || Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
//      if (!AdConfig.getInstance().ad_listen_apps) {
//        return;
//      }
//      Uri packageUri = intent.getData();
//      String packageName = packageUri.getSchemeSpecificPart();
//
//
//      if (Intent.ACTION_PACKAGE_ADDED.equals(action)
//              && !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
//        // Add a package
//        Map<String, String> custome = new HashMap<String, String>();
//        custome.put(APP_PACKAGE, packageName);
//        Agent.onAd(mContext, APP_INSTALL, APP_LISTENER, null, null, custome);
//      } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)
//              && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
//        // Update a package
//        Map<String, String> custome = new HashMap<String, String>();
//        custome.put(APP_PACKAGE, packageName);
//        Agent.onAd(mContext, APP_UPDATE, APP_LISTENER, null, null, custome);
//      } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)
//              && !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
//        // Remove a package
//        Map<String, String> custome = new HashMap<String, String>();
//        custome.put(APP_PACKAGE, packageName);
//        Agent.onAd(mContext, APP_UNINSTALL, APP_LISTENER, null, null, custome);
//      }
//    } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
//      new Handler().postDelayed(new Runnable() {
//        public void run() {
//          Intent intent = new Intent();
//          intent.setClass(mContext, AgentService.class);
//          mContext.startService(intent);
//        }
//      }, 60 * 1000);
//    } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//      String netNow = CommonUtil.getNetworkType(context);
//
//      CommonUtil.printLog("CONNECTIVITY_ACTION", netNow);
//      //网络切换到可用状态时候,且上一次拿发送策略拿失败
//      if (AgentConstants.NET_TYPE_WIFI.equals(netNow) || AgentConstants.NET_TYPE_MB.equals(netNow)) {
//        if (!fetch_config_get_success) {
//          CommonUtil.printLog("config", "retry to fetch config");
//          new Thread(new Runnable() {
//            public void run() {
//              getConfigFromServer(fetch_config_level_normal);
//            }
//          }).start();
//        }
//        //每3个小时，尝试去发送之前没发送成功的本地文件
//        CommonUtil.printLog("PostWhenNetWork", "interval is :" + (System.currentTimeMillis() - last_try_send_fail_files));
//        if (System.currentTimeMillis() - last_try_send_fail_files >= ConfigConstants.TRY_RESEND_FAILFILES_INTERVAL) {
//          PostWhenNetWork();
//          last_try_send_fail_files = System.currentTimeMillis();
//        }
//      } else {
//        CommonUtil.printLog("CONNECTIVITY_ACTION", "no netWork");
//      }
//    }
//
//  }
//
//  protected void doHeartbeat() {
//    String ret = HttpUtil.sendGet(AgentService.getHeartBeatUrl(), AgentConstants.APP_KEY + "=" + AgentData.appkey
//            + "&" + AgentConstants.DUID + "=" + AgentData.deviceUId);
//    CommonUtil.printLog("doHeartbeat", ret);
//
//  }
//
//  private synchronized void getConfigFromServer(String level) {
//    CommonUtil.printLog("getConfigFromServer", level + " " + last_get_config_time + " interval:" + (System.currentTimeMillis() - last_get_config_time));
//    //24h一次拿配置，会有这个4h的限制（失败重试的情况）
//    if (Math.abs(System.currentTimeMillis() - last_get_config_time) < ConfigConstants.GET_CONFIG_INTERVAL && fetch_config_level_normal.equals(level)) {
//      CommonUtil.printLog("getConfigFromServer", "interval too short.");
//      return;
//    }
//
//    last_get_config_time = System.currentTimeMillis();
//    CommonUtil.printLog("getConfigFromServer", "set last_get_config_time:" + System.currentTimeMillis());
//    AgentData.init(mContext);
//    String vs = MD5Util.md5(AgentData.appkey + AgentData.deviceUId + ConfigConstants.keymap.get(AgentData.appkey));
//    String param0 = "app_key=" + AgentData.appkey + "&duid=" + AgentData.deviceUId + "&vs=" + vs;
//    String ret = HttpUtil.sendGet(AgentService.getConfigUrl(), param0);
//    if (ret == null || "".equals(ret)) {
//      CommonUtil.printErrorLog("getConfigFromServer", "ret is null");
//      fetch_config_get_success = false;
//      CommonUtil.saveBoolean(mContext, fetch_config_get_success, DataConstants.FETCH_CONFIG_GET_SUCCESS);
//      CommonUtil.printLog("getConfigFromServer return null ", "turnOffAllFeatures");
//      FeatureConfig.getInstance().turnOffAllFeatures(mContext);
//    } else {
//      CommonUtil.printLog("config json", ret);
//      try {
//        JSONObject conf = new JSONObject(ret);
//
//        JSONObject dataconf = conf.getJSONObject(ConfigConstants.event);
//        isrestartevent = !(EventConfig.getInstance().event_switch && dataconf.getInt("switch") == 1
//                && EventConfig.getInstance().event_interval == dataconf.getInt("interval"));
//        dataconf = conf.getJSONObject(ConfigConstants.word);
//        isrestartword = !(WordConfig.getInstance().word_switch && dataconf.getInt("switch") == 1
//                && WordConfig.getInstance().word_interval == dataconf.getInt("interval"));
//        dataconf = conf.getJSONObject(ConfigConstants.heartbeat);
//        isrestarthb = !(HeartBeatConfig.getInstance().heartbeat_switch && dataconf.getInt("switch") == 1
//                && HeartBeatConfig.getInstance().heartbeat_interval == dataconf.getInt("interval"));
//        dataconf = conf.getJSONObject(ConfigConstants.abtest);
//        isrestartab = !(ABTestConfig.getInstance().ab_switch && dataconf.getInt("switch") == 1
//                && ABTestConfig.getInstance().ab_interval == dataconf.getInt("interval"));
//        DataConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.data));
//        AdConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.ad));
//        ErrorConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.error));
//        MetaConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.meta));
//        EventConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.event));
//        WordConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.word));
//        HeartBeatConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.heartbeat));
//        ABTestConfig.getInstance().saveConfig(mContext, conf.getJSONObject(ConfigConstants.abtest));
//        FeatureConfig.getInstance().saveConfig(mContext, conf.has(ConfigConstants.feature) ? conf.getJSONObject(ConfigConstants.feature) : null);
//
//        fetch_config_get_success = true;
//        CommonUtil.saveBoolean(mContext, fetch_config_get_success, DataConstants.FETCH_CONFIG_GET_SUCCESS);
//        CancelConfigAlarm();
//        cancelAlarm(mContext, ALARM_ACTION_FETCH_CONFIG);
//        new Thread(new Runnable() {
//          public void run() {
//            try {
//              Thread.sleep(100);
//              AgentService.startAlarms(mContext);
//              AgentReceiver.setAlarm(mContext, ALARM_ACTION_FETCH_CONFIG, FETCH_CONFIG_INTERVAL,
//                      FETCH_CONFIG_INTERVAL);
//            } catch (InterruptedException e) {
//              e.printStackTrace();
//            }
//          }
//        }).start();
//
//        if (fetch_config_level_force.equals(level)) {
//          Map<String, String> f_map = new HashMap<>();
//          f_map.put("fid", FeatureConfig.getInstance().getFeature_id());
//          Agent.onEvent(mContext, "fetch_config_force", "turnoff", "item", f_map);
//        }
//
//      } catch (JSONException e) {
//        CommonUtil.printErrorLog("config JSON error", e.getMessage());
//        fetch_config_get_success = false;
//        CommonUtil.saveBoolean(mContext, fetch_config_get_success, DataConstants.FETCH_CONFIG_GET_SUCCESS);
//      }
//    }
//  }
//
//  /*
//   * cancel alarm where action
//   */
//  public static void cancelAlarm(Context context, String action) {
//    try {
//      AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//      Intent intent = new Intent(context, AgentReceiver.class);
//      intent.setAction(action);
//      PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//      alarm.cancel(pi);
//    } catch (Exception e) {
//      // TODO: handle exception
//    }
//  }
//
//  public void CancelConfigAlarm() {
//    if (!EventConfig.getInstance().event_switch || isrestartevent) {
//      cancelAlarm(mContext, AgentReceiver.ALARM_ACTION_EVENT);
//    }
//    if (!HeartBeatConfig.getInstance().heartbeat_switch || isrestarthb) {
//      cancelAlarm(mContext, AgentReceiver.ALARM_ACTION_HEARTBEAT);
//    }
//    if (!WordConfig.getInstance().word_switch || isrestartword) {
//      cancelAlarm(mContext, AgentReceiver.ALARM_ACTION_WORD);
//    }
//    if (!ABTestConfig.getInstance().ab_switch || isrestartab) {
//      cancelAlarm(mContext, AgentReceiver.ALARM_ACTION_ABTEST);
//    }
//  }
//
//  /*
//   * set alarm with AlarmManager.ELAPSED_REALTIME_WAKEUP
//   */
//  public static void setAlarm(Context context, String action, long StartTime, long IntervalTime) {
//    try {
//      AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//      Intent intent = new Intent(context, AgentReceiver.class);
//      intent.setAction(action);
//      PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//      if (AgentConstants.debugMode) {
//        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + StartTime, IntervalTime, pi);
//      } else {
//        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + StartTime,
//                IntervalTime, pi);
//      }
//    } catch (Exception e) {
//      // TODO: handle exception
//    }
//  }
//
//  public void PostWhenNetWork() {
//
////    CommonUtil.printLog("PostWhenNetWork getFileSendStatus ", TypeConstants.META_TYPE + "  " + FileInfo.getFileSendStatus(TypeConstants.META_TYPE));
//    //既然每次doPostFromFile都会让meta ad error 发送一次，这里就没必要调用三次meta ad error
//    AgentService.postFromFile(mContext, TypeConstants.META_TYPE, AgentService.POSTCMD_SOURCE_NETCHANGE);
//
//    //如果上一次发送失败
//    if (!FileInfo.getFileSendStatus(TypeConstants.OPERATE_TYPE)) {
//      AgentService.postFromFile(mContext, TypeConstants.OPERATE_TYPE, AgentService.POSTCMD_SOURCE_NETCHANGE);
//    }
//    //如果上一次发送失败
//    if (!FileInfo.getFileSendStatus(TypeConstants.WORD_TYPE)) {
//      AgentService.postFromFile(mContext, TypeConstants.WORD_TYPE, AgentService.POSTCMD_SOURCE_NETCHANGE);
//    }
//  }
//}
