package com.phicomm.smartplug.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class WifiUtils {
    /**
     * 获取当前的 SSID
     *
     * @param context
     * @return
     */
    public static String getCurrentSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return getCurrentSSID(wifiManager);
    }

    /**
     * 获取当前的 SSID
     *
     * @param wifiManager
     * @return
     */
    public static String getCurrentSSID(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "";
        }

        String wifiSSID = wifiInfo.getSSID();
        wifiSSID = stripSSIDQuotes(wifiSSID);

        return wifiSSID;
    }

    /**
     * 去除 SSID 前后的双引号
     *
     * @param ssid
     * @return
     */
    public static String stripSSIDQuotes(String ssid) {
        if (!TextUtils.isEmpty(ssid) && (ssid.charAt(0) == '"')) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * 获取当前Wi-Fi信道（2.4G, 5G）
     *
     * @param context
     * @return
     */
    public static int getCurrentChannel(Context context) {
        int frequency = -1;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();// 当前wifi连接信息
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            frequency = wifiInfo.getFrequency();
        } else {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult result : scanResults) {
                if (result.BSSID.equalsIgnoreCase(wifiInfo.getBSSID())
                        && result.SSID.equalsIgnoreCase(wifiInfo.getSSID()
                        .substring(1, wifiInfo.getSSID().length() - 1))) {
                    frequency = result.frequency;
                }
            }
        }
        return frequency;
    }

    public static boolean isChannel24G(int frequency) {
        return Integer.toString(frequency).startsWith("2");
    }

}
