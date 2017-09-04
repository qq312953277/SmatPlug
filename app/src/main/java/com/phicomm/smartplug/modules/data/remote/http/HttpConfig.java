package com.phicomm.smartplug.modules.data.remote.http;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public class HttpConfig {

    /***************** HTTP Base Url*********************/
    //cloud base url
//    public static final String PHICOMM_CLOUD_BASE_URL = "http://114.141.173.27:10006/v1/";
//    public static final String PHICOMM_CLOUD_BASE_URL = "http://114.141.173.53/v1/";
    public static final String PHICOMM_CLOUD_BASE_URL = "https://account.phicomm.com/v1/";

    //avatar base url
//    public static final String PHICOMM_AVATER_BASE_URL = "http://114.141.173.27:10006/pic/";
    public static final String PHICOMM_AVATER_BASE_URL = "https://portrait.phicomm.com/pic/";


    //ota base url
    public static final String PHICOMM_OTA_BASE_URL = "https://phiclouds.phicomm.com/ota/";
//    public static final String PHICOMM_OTA_BASE_URL = "http://222.73.156.117/ota/";

    /*add by yangfeilong*/
    //设备信息测试环境
    public static final String PHICLOUDS_BASE_TEST_URL = "https://phiclouds.phicomm.com/device/";
//    public static final String PHICLOUDS_BASE_TEST_URL = "https://Smartplug.phicomm.com/device/";

    //设备管控baseurl
    public static final String PHICLOUDS_DEVICE_CONTROL_URL = "https://Smartplug.phicomm.com/SmartPlugAppV1/plug/";

    //scene base url
    public static final String PHICOMM_SCENE_BASE_URL = "https://Smartplug.phicomm.com/SmartPlugScenario/";
}
