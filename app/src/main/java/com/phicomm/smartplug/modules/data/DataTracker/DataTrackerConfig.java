package com.phicomm.smartplug.modules.data.DataTracker;

import com.phicomm.smartplug.modules.device.devicedetails.PowerStatisticsActivity;
import com.phicomm.smartplug.modules.mainpage.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yun.wang on 2017/8/16 0016.
 */

public class DataTrackerConfig {

    //EventID，添加在此处，同时需要在后台先添加对应的EVENT_ID
    public static final String EVENT_LOGIN_LOGOUT = "event_loginlogout";
    public static final String EVENT_NETWORK_FAIL = "event_networkfailreason";

    public static final String EVENT_DEVICE = "event_device";
    public static final String EVENT_DEVICE_CONTROL = "event_device_control";

    // umeng
    public static final String EVENT_SCENE = "event_scene";
    public static final String EVENT_UPDATE = "event_update";

    //含有viewpageFragment的Activity列表名称
    public static List<String> fragmentActivityList = new ArrayList<>();

    public static void init() {
        if (fragmentActivityList == null) {

        } else {
            fragmentActivityList.clear();

            //所有含有viewpageFragment的Activity，添加在此处，避免umeng重复统计session
            fragmentActivityList.add(MainActivity.class.getSimpleName());
            fragmentActivityList.add(PowerStatisticsActivity.class.getSimpleName());
        }
    }

    /**
     * 返回所有含有viewpageFragment的Activity列表名称
     *
     * @return
     */
    public static List<String> getActivityNameContainsViewPageFragment() {

        return fragmentActivityList;
    }
}
