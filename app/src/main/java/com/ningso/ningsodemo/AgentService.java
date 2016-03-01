//package com.ningso.ningsodemo;
//
//import java.io.File;
//import java.lang.System;
//import java.util.Map;
//import java.util.Random;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import com.qisi.datacollect.FileInfo.FileInfo;
//import com.qisi.datacollect.config.*;
//import com.qisi.datacollect.receiver.AgentReceiver;
//import com.qisi.datacollect.sdk.Agent;
//import com.qisi.datacollect.sdk.object.AgentArgFlag;
//import com.qisi.datacollect.sdk.object.AgentData;
//import com.qisi.datacollect.sdk.common.AgentConstants;
//import com.qisi.datacollect.sdk.common.CommonUtil;
//import com.qisi.datacollect.sdk.common.DataConstants;
//import com.qisi.datacollect.sdk.common.TypeConstants;
//import com.qisi.datacollect.sdk.controller.Controller;
//import com.qisi.datacollect.sdk.controller.TempController;
//import com.qisi.datacollect.sdk.controller.WordController;
//import com.qisi.datacollect.sdk.object.*;
//import com.qisi.datacollect.util.Log;
//import com.qisi.datacollect.util.MD5Util;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//
//public class AgentService extends Service {
//    private static final String TAG = "AgentService";
//
//    private ExecutorService mExec;
//    private BroadcastReceiver receiver;
//    private int mThreadNum;
//    private static final int THREAD_NUM = 2;
//    private Handler mHandler;
//    private Random mSamplingRandomGenerator;
//    private static final int SAMPLING_BASE = 1000;
//    // AD constant string
//    private static String AD_ITEMID = "ad_itemId";
//    private static String AD_LAYOUT = "ad_layout";
//    private static String AD_PRELAYOUT = "ad_preLayout";
//    private static String AD_TRIGGERTYPE = "ad_triggerType";
//    private static String AD_CUSTOME = "ad_custome";
//
//    // Error constant string
//    private static String ERROR = "error";
//    private static String ERROR_ID = "error_id";
//    private static String ERROR_TYPE = "error_type";
//
//    // Event constant string
//    private static String EVENT_LAYOUT = "event_layout";
//    private static String EVENT_ITEMID = "event_itemId";
//    private static String EVENT_OPERATETYPE = "event_operateType";
//    private static String EVENT_CUSTOME = "event_custome";
//    private static String EVENT_FLAG = "event_flag";
//
//    // Word constant string
//    private static String WORD_LANGUAGE_POS = "word_language_pos";
//    private static String WORD_WORD = "word_word";
//    private static String WORD_APPLICATION = "word_application";
//    private static String WORD_TRACECONTENT = "word_traceContent";
//    private static String DICT_VERSION = "word_dict_version";
//    private static String WORD_STATUS = "word_dict_status";
//
//    public static String CMD_SOURCE = "cmd_src";
//    public static String POSTCMD_SOURCE_ALARM = "cmd_from_alarm";
//    public static String POSTCMD_SOURCE_NETCHANGE = "cmd_from_netchange";
//
//
//    // PostFromFile constant string
//    private static String MSG_TYPE = "msg_type";
//
//    // API Action String
//    private static String ACTION_AD = "com.qisi.datacollect.service.aciton_ad";
//    private static String ACTION_ERROR = "com.qisi.datacollect.service.aciton_error";
//    private static String ACTION_EVENT = "com.qisi.datacollect.service.aciton_event";
//    private static String ACTION_META = "com.qisi.datacollect.service.aciton_meta";
//    private static String ACTION_WORD = "com.qisi.datacollect.service.aciton_word";
//    private static String ACTION_POST_FROM_FILE = "com.qisi.datacollect.service.aciton_post_from_file";
//    private static String ACTION_FORCE_UPDATE_STRATEGY = "com.qisi.datacollect.service.aciton_force_update_strategy";
//
//
//    public static void setConfigUrl(String url) {
//        AgentConstants.configurl = url;
//    }
//
//    public static String getConfigUrl() {
//        return AgentConstants.configurl;
//    }
//
//    public static void setHeartBeatUrl(String url) {
//        AgentConstants.heartbeatUrl = url;
//    }
//
//    public static String getHeartBeatUrl() {
//        return AgentConstants.heartbeatUrl;
//    }
//
//    /*
//     * register message
//     */
//    public void registerBroadcastReceiver() {
//        receiver = new AgentReceiver();
//
//        // Package change listener
//        IntentFilter filter2 = new IntentFilter();
//        filter2.addAction("android.intent.action.PACKAGE_ADDED");
//        filter2.addAction("android.intent.action.PACKAGE_CHANGED");
//        filter2.addAction("android.intent.action.PACKAGE_REPLACED");
//        filter2.addAction("android.intent.action.PACKAGE_REMOVED");
//        filter2.addDataScheme("package");
//        registerReceiver(receiver, filter2);
//
//        // Network status change listener
//        IntentFilter filter3 = new IntentFilter();
//        filter3.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(receiver, filter3);
//    }
//
//    private void unregisterBroadcastReceiver() {
//        unregisterReceiver(receiver);
//    }
//
//    // 初始化一些变量和配置
//    public void init() {
//        AgentData.init(getApplicationContext());
//
//        AdConfig.getInstance().init(getApplicationContext());
//
//        MetaConfig.getInstance().init(getApplicationContext());
//
//        ErrorConfig.getInstance().init(getApplicationContext());
//
//        EventConfig.getInstance().init(getApplicationContext());
//
//        WordConfig.getInstance().init(getApplicationContext());
//
//        HeartBeatConfig.getInstance().init(getApplicationContext());
//
//        DataConfig.getInstance().init(getApplicationContext());
//
//        ABTestConfig.getInstance().init(getApplicationContext());
//        FeatureConfig.getInstance().init(getApplicationContext());
//
//    }
//
//    @Override
//    public void onCreate() {
//
//        CommonUtil.printLog("========Agent Service onCreate begin==========", "debug\t" + AgentConstants.debugMode);
//
//        // Init thread pool which use to upload message to server
//        mThreadNum = THREAD_NUM;
//        ThreadFactory factory = createThreadFactory(Thread.NORM_PRIORITY, "kika-agent-");
//        mExec = Executors.newFixedThreadPool(mThreadNum, factory);
//        // Init a HandlerThread which use to save message to file
//        HandlerThread localHandlerThread = new HandlerThread("kika-Agent-savefile");
//        localHandlerThread.start();
//        mHandler = new Handler(localHandlerThread.getLooper());
//        // Init
//        init();
//        mSamplingRandomGenerator = new Random();
//        // Register broadcast receiver
//        registerBroadcastReceiver();
//        // Start dataAlarm
//        startAlarms(getApplicationContext());
//        // StartFetchConfigAlarms
//        startFetchConfigAlarms(getApplicationContext());
//        super.onCreate();
//
//        CommonUtil.printLog("========Agent Service onCreate completed=====", "debug\t" + AgentConstants.debugMode);
//    }
//
//    public static void startFetchConfigAlarms(Context context) {
//        int starttime = 5 * 1000;
//        if (AgentConstants.debugMode) {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_FETCH_CONFIG, starttime,
//                    AgentReceiver.FETCH_CONFIG_INTERVAL_DEBUG);
//        } else {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_FETCH_CONFIG, starttime,
//                    AgentReceiver.FETCH_CONFIG_INTERVAL);
//        }
//    }
//
//    public static void startAlarms(Context context) {
//        if (!DataConfig.getInstance().data_switch) {
//            return;
//        }
//        int starttime = 30 * 1000;
//        if (EventConfig.getInstance().event_switch && AgentReceiver.isrestartevent) {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_EVENT, starttime,
//                    EventConfig.getInstance().event_interval);
//        }
//        if (HeartBeatConfig.getInstance().heartbeat_switch && AgentReceiver.isrestarthb) {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_HEARTBEAT, starttime,
//                    HeartBeatConfig.getInstance().heartbeat_interval);
//        }
//        if (WordConfig.getInstance().word_switch && AgentReceiver.isrestartword) {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_WORD, starttime,
//                    WordConfig.getInstance().word_interval);
//        }
//        if (ABTestConfig.getInstance().ab_switch && AgentReceiver.isrestartab) {
//            AgentReceiver.setAlarm(context, AgentReceiver.ALARM_ACTION_ABTEST, starttime,
//                    ABTestConfig.getInstance().ab_interval);
//        }
//
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.i(TAG, "onDestroy()");
//
//        unregisterBroadcastReceiver();
//
//        mExec.shutdown();
//        super.onDestroy();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
//    // 检查event的oid类型来确认是否发送该类型数据
//    public boolean checkEventOid(String oid) {
//
//        // priority :
//        // 1) if oid_in is not empty, only send oid in it
//        // 2) if oid_in is empty,
//        //    -> if the oid is in the oid_out list, DO NOT send it
//        //    -> if the oid is not in the oid_out list, check if existing in sampling list
//
//        boolean bCheckRatio = false;
//        if (EventConfig.getInstance().event_oid_in.isEmpty()) {
//            if (EventConfig.getInstance().event_oid_out.contains(oid)) {
//                return false;
//            } else {
//                if (EventConfig.getInstance().event_oid_sampling.containsKey(oid))
//                    bCheckRatio = true;
//                else
//                    return true;
//            }
//        } else {
//            if (EventConfig.getInstance().event_oid_sampling.containsKey(oid))
//                bCheckRatio = true;
//            else
//                return true;
//        }
//
//        if (bCheckRatio) {
//            CommonUtil.printLog("checkEventOid", "need checkout ratio\t" + oid);
//            int ratio = EventConfig.getInstance().event_oid_sampling.get(oid);
//            // not in the range, ignore the event
//            if (ratio < 0 || ratio > SAMPLING_BASE)
//                return false;
//            mSamplingRandomGenerator.setSeed(System.currentTimeMillis());
//            if (mSamplingRandomGenerator.nextInt(SAMPLING_BASE) < ratio)
//                return true;
//        }
//
//        return false;
//    }
//
//    // 检查是否数据发送超量溢出
//    public static void dataOverflow(String type, Context context) {
//        String overflow = "";
//        if (type.equals(TypeConstants.WORD_TYPE)) {
//            if (AgentData.data_wordoverflow) {
//                return;
//            }
//            if (AgentData.dayworddatatotal >= WordConfig.getInstance().word_size_threshold) {
//                AgentData.data_wordoverflow = true;
//                CommonUtil.saveBoolean(context, AgentData.data_wordoverflow, DataConstants.DAY_WORD_OVER_FLOW);
//            } else {
//                return;
//            }
//            overflow = "threshold" + WordConfig.getInstance().word_size_threshold;
//        } else if (type.equals(TypeConstants.OPERATE_TYPE)) {
//            if (AgentData.data_eventoverflow) {
//                return;
//            }
//            if (AgentData.dayeventdatatotal >= EventConfig.getInstance().event_size_threshold) {
//                AgentData.data_eventoverflow = true;
//                CommonUtil.saveBoolean(context, AgentData.data_eventoverflow, DataConstants.DAY_EVENT_OVER_FLOW);
//            } else {
//                return;
//            }
//            overflow = "threshold=" + EventConfig.getInstance().event_size_threshold;
//        }
//        overflow += "up_sdk_type=" + type;
//        Agent.onError(context, overflow);
//    }
//
//    // 检查各种数据发送开关
//    public boolean checkSend(String action) {
//        if (!DataConfig.getInstance().isSend(getApplicationContext())) {
//            return false;
//        }
//        if (action.equals(ACTION_AD)) {
//            return AdConfig.getInstance().isSend(getApplicationContext());
//        }
//        if (action.equals(ACTION_WORD)) {
//            if (!WordConfig.getInstance().isSend(getApplicationContext())) {
//                // Log.e(""+AgentData.dayworddatatotal);
//                if (WordConfig.getInstance().word_switch)
//                    dataOverflow(TypeConstants.WORD_TYPE, getApplicationContext());
//                return false;
//            } else {
//                return true;
//            }
//        }
//        if (action.equals(ACTION_EVENT)) {
//            if (!EventConfig.getInstance().isSend(getApplicationContext())) {
//                if (EventConfig.getInstance().event_switch)
//                    dataOverflow(TypeConstants.WORD_TYPE, getApplicationContext());
//                return false;
//            } else {
//                return true;
//            }
//        }
//        if (action.equals(ACTION_META)) {
//            return MetaConfig.getInstance().isSend(getApplicationContext());
//        }
//        return !action.equals(ACTION_ERROR) || ErrorConfig.getInstance().isSend(getApplicationContext());
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent == null) {
//            Log.e("onStartCommand(), service is restarted with null intent");
//            return START_STICKY;
//        }
//
//        String action = intent.getAction();
//        if (action == null) {
//            return START_STICKY;
//        }
//
//        if (!checkSend(action)) {
//            return START_STICKY;
//        }
//
//        if (ACTION_AD.equals(action)) {
//
//            Bundle bundle = intent.getExtras();
//            String itemId = bundle.getString(AD_ITEMID);
//            String layout = bundle.getString(AD_LAYOUT);
//            String preLayout = bundle.getString(AD_PRELAYOUT);
//            String triggerType = bundle.getString(AD_TRIGGERTYPE);
//            SerializableMap tempMap = (SerializableMap) bundle.get(AD_CUSTOME);
//            Map<String, String> custome = null;
//            if (tempMap != null) {
//                custome = tempMap.getMap();
//            }
//
//            // Just for develop use [S]
//            CommonUtil.printLog("ACTION_AD", "itemId:" + itemId + ", layout:" + layout + ", preLayout:" + preLayout + ", triggerType:"
//                    + triggerType);
//
//            doOnAd(itemId, layout, preLayout, triggerType, custome);
//        } else if (ACTION_ERROR.equals(action)) {
//
//            Bundle bundle = intent.getExtras();
//            String error = bundle.getString(ERROR);
//            String error_id = bundle.getString(ERROR_ID, "0");
//            String error_type = bundle.getString(ERROR_TYPE);
//
//
//            CommonUtil.printLog("ACTION_ERROR", "error: " + error_id + " " + error);
//
//            doOnError(error, error_id, error_type);
//        } else if (ACTION_EVENT.equals(action)) {
//
//            Bundle bundle = intent.getExtras();
//            String layout = bundle.getString(EVENT_LAYOUT);
//            String itemId = bundle.getString(EVENT_ITEMID);
//            String operateType = bundle.getString(EVENT_OPERATETYPE);
//
//
//            SerializableMap tempMap = (SerializableMap) bundle.get(EVENT_CUSTOME);
//            Map<String, String> custome = null;
//            if (tempMap != null) {
//                custome = tempMap.getMap();
//            }
//
//            String event_flag_str = bundle.getString(EVENT_FLAG, null);
//            AgentArgFlag[] event_flags = null;
//            if (event_flag_str != null) {
//                String[] items = event_flag_str.split("\t");
//                event_flags = new AgentArgFlag[items.length];
//                for (int i = 0; i < items.length; i++)
//                    event_flags[i] = AgentArgFlag.valueOf(items[i]);
//            }
//
//            CommonUtil.printLog("ACTION_EVENT", "layout:" + layout + ", itemId:" + itemId + ", operateType:" + operateType + "\tevent_flags " + event_flag_str);
//
//
//            doOnEvent(layout, itemId, operateType, null, null, null, 0, custome, event_flags);
//        } else if (ACTION_META.equals(action)) {
//            Bundle bundle = intent.getExtras();
//            String kbLang = null;
//            if (bundle != null)
//                kbLang = bundle.getString(JSONConstants.KEYBOARD_LANG, null);
//
//            CommonUtil.printLog("ACTION_META", "META,keyboard_lang\t" + kbLang);
//            doOnMeta(kbLang);
//        } else if (ACTION_WORD.equals(action)) {
//
//
//            Bundle bundle = intent.getExtras();
//            String language_pos = bundle.getString(WORD_LANGUAGE_POS);
//            String word = bundle.getString(WORD_WORD);
//            String application = bundle.getString(WORD_APPLICATION);
//            byte[] traceContent = bundle.getByteArray(WORD_TRACECONTENT);
//            String dictVersion = bundle.getString(DICT_VERSION);
//            String wordStatus = bundle.getString(WORD_STATUS);
//
//            CommonUtil.printLog("ACTION_WORD", "language_pos:" + language_pos + ", word:" + word + ", application:" + application + ", dictVersion:" + dictVersion + ", wordStatus:" + wordStatus);
//            doOnWord(language_pos, word, application, traceContent, dictVersion, wordStatus);
//        } else if (ACTION_POST_FROM_FILE.equals(action)) {
//
//
//            Bundle bundle = intent.getExtras();
//            String type = bundle.getString(MSG_TYPE);
//            String cmd_src = bundle.getString(CMD_SOURCE);
//            switch (type) {
//                case TypeConstants.WORD_TYPE:
//                    action = ACTION_WORD;
//                    break;
//                case TypeConstants.OPERATE_TYPE:
//                    action = ACTION_EVENT;
//                    break;
//                case TypeConstants.META_TYPE:
//                    action = ACTION_META;
//                    break;
//                case TypeConstants.ERROR_TYPE:
//                    action = ACTION_ERROR;
//                    break;
//                case TypeConstants.AD_TYPE:
//                    action = ACTION_AD;
//                    break;
//            }
//
//
//            if (!checkSend(action)) {
//                return START_STICKY;
//            }
//            doPostFromFile(type, cmd_src);
//        } else if (ACTION_FORCE_UPDATE_STRATEGY.equals(action)) {
//
//            doOnForceUpdateStrategy();
//        }
//        return START_STICKY;
//    }
//
//    public void doOnEvent(String layout, String itemId, String operateType, String category, String action,
//                          String label, int value, Map custome, AgentArgFlag[] flags) {
//        final String objectId = MD5Util.md5(AgentData.getAppkey(getApplicationContext()) + layout + itemId);
//        if (!checkEventOid(objectId)) {
//            return;
//        }
//        if (!AgentConstants.registerObject.isEmpty() && !AgentConstants.registerObject.contains(objectId)) {
//            return;
//        }
//        final String event = EventCreator.create(getApplicationContext(), objectId, layout, itemId, operateType,
//                category, action, label, value, (Map<String, String>) custome, flags);
//        Runnable postEventInfo = new Runnable() {
//            public void run() {
//                try {
//                    //如果debug模式，event也发实时
//                    if (AgentConstants.debugMode)
//                        Controller.postDebug(getApplicationContext(), TypeConstants.OPERATE_TYPE, event, null, mHandler);
//                    else if (AgentConstants.EVENT_REALTIME)
//                        Controller.post(getApplicationContext(), TypeConstants.OPERATE_TYPE, event, mHandler);
//                    else
//                        Controller.save(getApplicationContext(), TypeConstants.OPERATE_TYPE, event, mHandler);
//                } catch (Exception e) {
//                    Agent.onError(getApplicationContext(), e);
//                }
//            }
//        };
//        mExec.submit(postEventInfo);
//    }
//
//    public void doOnError(String error, String error_id, String error_type) {
//        final String errorJson = ErrorCreator.create(getApplicationContext(), error, error_id, error_type);
//        Runnable postErrorInfoRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (AgentConstants.debugMode)
//                        Controller.postDebug(getApplicationContext(), TypeConstants.ERROR_TYPE, errorJson, null, mHandler);
//                    else
//                        Controller.post(getApplicationContext(), TypeConstants.ERROR_TYPE, errorJson, mHandler);
//                } catch (Exception e) {
//                    // do nothing
//                }
//            }
//        };
//        mExec.submit(postErrorInfoRunnable);
//    }
//
//    public void doOnWord(String language_pos, String word, String application, byte[] traceContent, String dictVersion, String wordStatus) {
//        final String wordString = WordCreator.create(application, word, dictVersion, language_pos, wordStatus);
//        final byte[] trace = traceContent;
//        Runnable postWordRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    if (AgentConstants.debugMode)
//                        Controller.postDebug(getApplicationContext(), TypeConstants.WORD_TYPE, wordString, trace, mHandler);
//                    else
//                        WordController.post(getApplicationContext(), TypeConstants.WORD_TYPE, wordString, trace, mHandler);
//                } catch (Exception e) {
//                    Agent.onError(getApplicationContext(), e);
//                }
//            }
//        };
//        mHandler.post(postWordRunnable);
//    }
//
//    public void doOnAd(String itemId, String layout, String preLayout, String triggerType, Map custome) {
//        final String ad = AdCreator.create(MD5Util.md5(AgentData.getAppkey(getApplicationContext()) + layout + itemId),
//                itemId, layout, preLayout, triggerType, (Map<String, String>) custome);
//        Runnable postAdRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (AgentConstants.debugMode)
//                        Controller.postDebug(getApplicationContext(), TypeConstants.AD_TYPE, ad, null, mHandler);
//                    else
//                        Controller.post(getApplicationContext(), TypeConstants.AD_TYPE, ad, mHandler);
//                } catch (Exception e) {
//
//                    Agent.onError(getApplicationContext(), e);
//                }
//            }
//        };
//        mExec.submit(postAdRunnable);
//    }
//
//    public void doOnMeta(String kbLang) {
//        final String metaString = ClientMetaDataCreator.create(getApplicationContext(), kbLang, AgentData.thirdAccount,
//                AgentData.extendJson);
//        Runnable postMetaRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (AgentConstants.debugMode)
//                        Controller.postDebug(getApplicationContext(), TypeConstants.META_TYPE, metaString, null, mHandler);
//                    else
//                        Controller.post(getApplicationContext(), TypeConstants.META_TYPE, metaString, mHandler);
//                } catch (Exception e) {
//                    Agent.onError(getApplicationContext(), e);
//                }
//            }
//        };
//        mExec.submit(postMetaRunnable);
//    }
//
//    private void doOnForceUpdateStrategy() {
//        Random random = new Random();
//        long delay = 0l;
//        if (AgentConstants.debugMode)
//            delay = random.nextInt(5 * 1000); //5s内随机强制更新
//        else
//            delay = random.nextInt(3600 * 1000); //1小时内随机强制更新
//        CommonUtil.printLog("doOnForceUpdateStrategy", "rand " + delay);
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent();
//                Context c = getApplicationContext();
//                intent.setClass(c, AgentReceiver.class);
//                intent.setAction(AgentReceiver.ALARM_ACTION_FORCE_FETCH_CONFIG);
//                c.sendBroadcast(intent);
//            }
//        }, delay);
//    }
//
//
//    /**
//     * 这个方法每次被调用，都会去触发AD META 和 ERROR的发送
//     *
//     * @param type
//     */
//    private void doPostFromFile(String type, String cmdSource) {
//        Context context = getApplicationContext();
//
//        if (FileInfo.canPost(context, TypeConstants.AD_TYPE) > 0) {
//            CommonUtil.getInfoFromFile(mHandler, TypeConstants.AD_TYPE, context);
//        }
//        if (FileInfo.canPost(context, TypeConstants.META_TYPE) > 0) {
//            CommonUtil.getInfoFromFile(mHandler, TypeConstants.META_TYPE, context);
//        }
//        if (FileInfo.canPost(context, TypeConstants.ERROR_TYPE) > 0) {
//            CommonUtil.getInfoFromFile(mHandler, TypeConstants.ERROR_TYPE, context);
//        }
//
//        if (TypeConstants.WORD_TYPE.equals(type)) {
//            boolean netAvailable = AgentData.canSend(context, type);
//            if (POSTCMD_SOURCE_ALARM.equals(cmdSource)) { //定时发送命令,word需要flush内存然后发送。
//                boolean doHaveContent = TempController.flush(getApplicationContext(), type, mHandler);
//                CommonUtil.printLog("POSTCMD_SOURCE_ALARM", type + " netAvailable:" + netAvailable + " doHaveContent:" + doHaveContent + " hasFileofType:" + (FileInfo.hasFileofType(type) > 0));
//                if (!netAvailable && (doHaveContent || FileInfo.hasFileofType(type) > 0)) { //如果网络不允许，此次发送就失败，需要置setFileSendStatus为false
//                    FileInfo.setFileSendStatus(type, false);
//                } else if (netAvailable && (doHaveContent || FileInfo.hasFileofType(type) > 0)) { //如果网络允许且有需要发送的内容
//                    CommonUtil.getInfoFromFile(mHandler, type, context);
//                }
//            } else if (POSTCMD_SOURCE_NETCHANGE.equals(cmdSource)) {
//                CommonUtil.printLog("POSTCMD_SOURCE_NETCHANGE", type + " netAvailable:" + netAvailable);
//                if (netAvailable) {
//                    CommonUtil.getInfoFromFile(mHandler, type, context);
//                }
//            }
//        } else if (TypeConstants.OPERATE_TYPE.equals(type)) {
//            boolean netAvailable = AgentData.canSend(context, type);
//            if (POSTCMD_SOURCE_ALARM.equals(cmdSource)) {
//                CommonUtil.printLog("POSTCMD_SOURCE_ALARM", type + " netAvailable:" + netAvailable + " hasFileofType:" + (FileInfo.hasFileofType(type) > 0));
//                if (netAvailable && FileInfo.hasFileofType(type) > 0) { // 有事件需要发送
//                    CommonUtil.getInfoFromFile(mHandler, type, context);
//                } else if (!netAvailable && FileInfo.hasFileofType(type) > 0) {
//                    FileInfo.setFileSendStatus(type, false);
//                }
//            } else if (POSTCMD_SOURCE_NETCHANGE.equals(cmdSource)) {
//                CommonUtil.printLog("POSTCMD_SOURCE_NETCHANGE", type + " netAvailable:" + netAvailable);
//                if (netAvailable) {
//                    CommonUtil.getInfoFromFile(mHandler, type, context);
//                }
//            }
//        }
//    }
//
//    public static void onAd(final Context context, final String itemId, final String layout, final String preLayout,
//                            final String triggerType, final Map<String, String> custome) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_AD);
//        intent.setClass(context, AgentService.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(AD_ITEMID, itemId);
//        bundle.putString(AD_LAYOUT, layout);
//        bundle.putString(AD_PRELAYOUT, preLayout);
//        bundle.putString(AD_TRIGGERTYPE, triggerType);
//
//        SerializableMap tmpmap = new SerializableMap();
//        tmpmap.setMap(custome);
//        bundle.putSerializable(AD_CUSTOME, tmpmap);
//        intent.putExtras(bundle);
//        context.startService(intent);
//    }
//
//    public static void onError(final Context context, final String error, final String error_id, final String error_type) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_ERROR);
//        intent.setClass(context, AgentService.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(ERROR, error);
//        bundle.putString(ERROR_ID, error_id);
//        bundle.putString(ERROR_TYPE, error_type);
//        intent.putExtras(bundle);
//        context.startService(intent);
//    }
//
//    public static void onEvent(final Context context, final String layout, final String itemId,
//                               final String operateType, final Map<String, String> custome, AgentArgFlag... flags) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_EVENT);
//        intent.setClass(context, AgentService.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(EVENT_LAYOUT, layout);
//        bundle.putString(EVENT_ITEMID, itemId);
//        bundle.putString(EVENT_OPERATETYPE, operateType);
//
//
//        String flag_str = "";
//        for (AgentArgFlag flag : flags)
//            flag_str += flag.name() + "\t";
//
//        if (flag_str.length() > 0)
//            bundle.putString(EVENT_FLAG, flag_str);
//
//        SerializableMap tmpmap = new SerializableMap();
//        tmpmap.setMap(custome);
//        bundle.putSerializable(EVENT_CUSTOME, tmpmap);
//        intent.putExtras(bundle);
//
//        context.startService(intent);
//    }
//
//    public static void onMeta(final Context context, final String language) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_META);
//        intent.setClass(context, AgentService.class);
//
//        if (language != null) {
//            Bundle bundle = new Bundle();
//            bundle.putString(JSONConstants.KEYBOARD_LANG, language);
//            intent.putExtras(bundle);
//        }
//
//
//        context.startService(intent);
//    }
//
//    public static void onWord(final Context context, final String languagePos, final String word, final String application,
//                              final byte[] traceContent, final String dictVersion, final String wordStatus) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_WORD);
//        intent.setClass(context, AgentService.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(WORD_LANGUAGE_POS, languagePos);
//        bundle.putString(WORD_WORD, word);
//        bundle.putString(WORD_APPLICATION, application);
//        bundle.putByteArray(WORD_TRACECONTENT, traceContent);
//        bundle.putString(DICT_VERSION, dictVersion);
//        bundle.putString(WORD_STATUS, wordStatus);
//        intent.putExtras(bundle);
//        context.startService(intent);
//    }
//
//    public static void postFromFile(final Context context, final String type, final String cmd_src) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_POST_FROM_FILE);
//        intent.setClass(context, AgentService.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(MSG_TYPE, type);
//        bundle.putString(CMD_SOURCE, cmd_src);
//        intent.putExtras(bundle);
//        context.startService(intent);
//    }
//
//
//    public static void startKoalaService(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context, AgentService.class);
//        context.startService(intent);
//    }
//
//    public static void forceUpdateStrategy(Context context) {
//
//        Intent intent = new Intent();
//        intent.setAction(ACTION_FORCE_UPDATE_STRATEGY);
//        intent.setClass(context, AgentService.class);
//        context.startService(intent);
//    }
//
//
//    /**
//     * Creates thread factory
//     */
//    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
//        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
//    }
//
//    private static class DefaultThreadFactory implements ThreadFactory {
//
//        private static final AtomicInteger poolNumber = new AtomicInteger(1);
//
//        private final ThreadGroup group;
//        private final AtomicInteger threadNumber = new AtomicInteger(1);
//        private final String namePrefix;
//        private final int threadPriority;
//
//        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
//            this.threadPriority = threadPriority;
//            group = Thread.currentThread().getThreadGroup();
//            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
//        }
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
//            if (t.isDaemon())
//                t.setDaemon(false);
//            t.setPriority(threadPriority);
//            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//
//                @Override
//                public void uncaughtException(Thread thread, Throwable ex) {
//                    ex.printStackTrace();
//                }
//            });
//            return t;
//        }
//    }
//}
