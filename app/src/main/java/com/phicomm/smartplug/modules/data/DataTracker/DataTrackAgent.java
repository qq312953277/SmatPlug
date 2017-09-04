package com.phicomm.smartplug.modules.data.DataTracker;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by yun.wang on 2017/8/11 0011.
 */
public class DataTrackAgent {

    /**
     * init
     *
     * @param context
     */
    public static void init(Context context) {
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);

        //自定义session统计事件，默认是30s
//        MobclickAgent.setSessionContinueMillis(60 * 1000);

        //禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);

        //程序崩溃日志捕获
        MobclickAgent.setCatchUncaughtExceptions(true);

        //是否打开U-App调试模式 MobclickAgent
        MobclickAgent.setDebugMode(true);

        DataTrackerConfig.init();
    }

    /**
     * onPageStart,called by activity or fragment
     *
     * @param pagename
     */
    public static void onPageStart(String pagename) {
        MobclickAgent.onPageStart(pagename);
    }

    /**
     * onPageEnd, called by activity or fragment
     *
     * @param pagename
     */
    public static void onPageEnd(String pagename) {
        MobclickAgent.onPageEnd(pagename);
    }

    /**
     * onResume, called by activity
     *
     * @param context
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * onPause, called by activity
     *
     * @param context
     */
    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    /**
     * 发送自定义数据数据到友盟（计数事件）
     * <p>
     * 自定义事件的代码需要放在Activity里的onResume--onPause之间，请在友盟初始化之后调用事件,不支持在service中统计。
     * 使用自定义事件功能请先登陆友盟官网 ， 在 “统计分析->设置->事件”
     * 页面中添加相应的事件id（事件id可用英文或数字，不要使用中文和特殊字符且不能使用英文句号“.”您可以使用下划线“_”），
     * 然后服务器才会对相应的事件请求进行处理。
     *
     * @param context
     * @param eventId :事件ID，需在后台提前定义
     * @param map     ：数据内容（key-value键值对）
     */
    public static void commitCountEvent2Umeng(Context context,
                                              String eventId,
                                              HashMap map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

    /**
     * 发送自定义数据数据到友盟（计数事件），只有一个键值对
     *
     * @param context
     * @param eventId
     * @param key
     * @param value
     */
    public static void commitCountEvent2Umeng(Context context,
                                              String eventId,
                                              String key,
                                              String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);

        commitCountEvent2Umeng(context, eventId, map);
    }

    /**
     * 发送自定义数据数据到友盟（计算事件）
     * <p>
     * 自定义事件的代码需要放在Activity里的onResume--onPause之间，请在友盟初始化之后调用事件,不支持在service中统计。
     * 使用自定义事件功能请先登陆友盟官网 ， 在 “统计分析->设置->事件”
     * 页面中添加相应的事件id（事件id可用英文或数字，不要使用中文和特殊字符且不能使用英文句号“.”您可以使用下划线“_”），
     * 然后服务器才会对相应的事件请求进行处理。
     *
     * @param context
     * @param eventId :事件ID，需在后台提前定义
     * @param map     ：数据内容（key-value键值对）
     * @param du      ：时长
     */
    public static void commitValueEvent2Umeng(Context context,
                                              String eventId,
                                              HashMap map,
                                              int du) {
        MobclickAgent.onEventValue(context, eventId, map, du);
    }

    /**
     * 发送自定义数据数据到友盟（计算事件），只有一个键值对
     *
     * @param context
     * @param eventId
     * @param key
     * @param value
     * @param du
     */
    public static void commitValueEvent2Umeng(Context context,
                                              String eventId,
                                              String key,
                                              String value,
                                              int du) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);

        commitValueEvent2Umeng(context, eventId, map, du);
    }


    /**
     * 自定义的error report
     *
     * @param context
     * @param error
     */
    public static void reportError(Context context, String error) {
        MobclickAgent.reportError(context, error);
    }

    /**
     * 自定义的error report
     *
     * @param context
     * @param e
     */
    public static void reportError(Context context, Throwable e) {
        MobclickAgent.reportError(context, e);
    }

    /**
     * 统计账号信息，需要在后台“我的产品-设置-应用信息”打开“使用账号统计报表”
     *
     * @param userId ：用户账号ID，长度小于64字节
     */
    public static void commitAccount2Umeng(String userId) {
        MobclickAgent.onProfileSignIn(userId);
    }

    /**
     * 统计账号信息，需要在后台“我的产品-设置-应用信息”打开“使用账号统计报表”
     *
     * @param Provider ：账号来源，比如："WX","WB","QQ"
     * @param userId   ：用户账号ID，长度小于64字节
     */
    public static void commitAccount2Umeng(String Provider, String userId) {
        MobclickAgent.onProfileSignIn(userId);
    }
}
