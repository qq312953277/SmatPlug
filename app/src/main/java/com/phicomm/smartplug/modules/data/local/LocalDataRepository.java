package com.phicomm.smartplug.modules.data.local;


import android.content.Context;

import com.phicomm.smartplug.modules.data.local.database.DatabaseDataRepository;
import com.phicomm.smartplug.modules.data.local.file.FileDataRepository;
import com.phicomm.smartplug.modules.data.local.preference.PreferenceDataRepository;
import com.phicomm.smartplug.modules.data.local.preference.PreferenceDef;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;

import java.util.ArrayList;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public class LocalDataRepository {

    private static final String TAG = "LocalDataRepository";
    private Context mContext;

    private DatabaseDataRepository mDatabaseDataRepository;
    private FileDataRepository mFileDataRepository;
    private PreferenceDataRepository mPreferenceDataRepository;

    public LocalDataRepository(Context context) {
        mContext = context;
        mDatabaseDataRepository = new DatabaseDataRepository(context);
        mFileDataRepository = new FileDataRepository(context);
        mPreferenceDataRepository = new PreferenceDataRepository(context);
    }


    /******************************DataBase Repository Start**********************************/
    public void resetLocalDateBase() {
        mDatabaseDataRepository.resetLocalDateBase();
    }

    public String getSettingValue(String name, String defaultValue) {
        return mDatabaseDataRepository.getSettingValue(name, defaultValue);
    }

    public void setSettingsValue(String name, String value) {
        mDatabaseDataRepository.setSettingsValue(name, value);
    }

    /**
     * 增
     *
     * @param deviceBean
     */
    public void insert(DeviceListsBean.DeviceBean deviceBean) {
        mDatabaseDataRepository.insert(deviceBean);
    }

    /**
     * 更新设备
     *
     * @param deviceBean
     */
    public void updateDevice(DeviceListsBean.DeviceBean deviceBean) {
        mDatabaseDataRepository.updateDevice(deviceBean);
    }

    /**
     * 删
     *
     * @param deviceId
     */
    public void delete(String deviceId) {
        mDatabaseDataRepository.delete(deviceId);
    }

    public void deleteAll() {
        mDatabaseDataRepository.deleteAll();
    }

    /**
     * 修改设备名称
     *
     * @param deviceId
     * @param deviceName
     */
    public void update(String deviceId, String deviceName) {
        mDatabaseDataRepository.update(deviceId, deviceName);
    }

    /**
     * 修改设备插口名称
     *
     * @param bean
     */
    public void updateDeviceSwitchName(DeviceResultBean bean) {
        mDatabaseDataRepository.updateDeviceSwitchName(bean);
    }

    /**
     * 查
     *
     * @return
     */
    public ArrayList<DeviceListsBean.DeviceBean> query() {
        return mDatabaseDataRepository.query();
    }
    /******************************DataBase Repository end************************************/


    /******************************SharedPreference Repository Start**************************/

    /**
     * 获取授权码
     */
    public String getAuthorizationCode() {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.AUTHORIZATION_CODE, "");
    }

    /**
     * 设置授权码
     */
    public void setAuthorizationCode(String code) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.AUTHORIZATION_CODE, code);
    }

    /**
     * 云端之前是否已经登录了
     *
     * @return 登录状态
     */
    public boolean isCloudLogined() {
        return (boolean) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_LOGIN_STATUS, false);
    }

    /**
     * 设置云端的登录状态
     *
     * @param loginStatus 已登录为true，已登出为false
     */
    public void setCloudLoginStatus(boolean loginStatus) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_LOGIN_STATUS, loginStatus);
    }

    /**
     * app是否引导过
     *
     * @return
     */
    public boolean isAppGuided() {
        return (boolean) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.APP_GUIDE, false);
    }

    /**
     * 记录下app已经被用户引导过了
     */
    public void setAppGuided(boolean isguided) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.APP_GUIDE, isguided);
    }

    /**
     * 设置用户的基本信息
     */
    public void setAccountDetailInfo(AccountDetailBean accountDetailBean) {
        mPreferenceDataRepository.saveObj(PreferenceDef.SP_NAME_USER_COOKIE, AccountDetailBean.class.getSimpleName(), accountDetailBean);
    }

    /**
     * 获取用户的基本信息
     *
     * @return
     */
    public AccountDetailBean getAccountDetailInfo() {
        return (AccountDetailBean) mPreferenceDataRepository.getObj(PreferenceDef.SP_NAME_USER_COOKIE, AccountDetailBean.class.getSimpleName());
    }

    /**
     * getAccessToken
     *
     * @return
     */
    public String getAccessToken() {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN, "");
    }

    /**
     * setAccessToken
     */
    public void setAccessToken(String accessToken) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN, accessToken);
    }

    /**
     * getRefreshToken
     *
     * @return
     */
    public String getRefreshToken() {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN, "");
    }

    /**
     * setRefreshToken
     *
     * @param refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN, refreshToken);
    }

    /**
     * getAccessValidity
     *
     * @return
     */
    public Long getAccessValidity() {
        return (Long) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN_VALIDITY, 0l);
    }

    /**
     * setAccessValidity
     *
     * @param accessValidity
     */
    public void setAccessValidity(Long accessValidity) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN_VALIDITY, accessValidity);
    }

    /**
     * getRefreshValidity
     *
     * @return
     */
    public Long getRefreshValidity() {
        return (Long) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN_VALIDITY, 0l);
    }

    /**
     * setRefreshValidity
     *
     * @param refreshValidity
     */
    public void setRefreshValidity(Long refreshValidity) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN_VALIDITY, refreshValidity);
    }

    /**
     * getRefreshStartTime
     *
     * @return
     */
    public Long getRefreshStartTime() {
        return (Long) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN_START_TIME, 0l);
    }

    /**
     * setRefreshStartTime
     *
     * @param refreshStartTime
     */
    public void setRefreshStartTime(Long refreshStartTime) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.REFRESH_TOKEN_START_TIME, refreshStartTime);
    }

    /**
     * V
     *
     * @return
     */
    public Long getAccessStartTime() {
        return (Long) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN_START_TIME, 0l);
    }

    /**
     * setAccessStartTime
     *
     * @param accessStartTime
     */
    public void setAccessStartTime(Long accessStartTime) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_TOKEN, PreferenceDef.ACCESS_TOKEN_START_TIME, accessStartTime);
    }

    /**
     * getUserName
     *
     * @return
     */
    public String getUserName() {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_USERNAME, "");
    }

    /**
     * setUsername
     *
     * @param username
     */
    public void setUsername(String username) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_USERNAME, username);
    }

    /**
     * getUserName
     *
     * @return
     */
    public String getPassword() {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_PASSWORD, "");
    }

    /**
     * setPassword
     *
     * @param password
     */
    public void setPassword(String password) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_PASSWORD, password);
    }

    /**
     * getRememberMe
     *
     * @return
     */
    public boolean getRememberMe() {
        return (boolean) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_REMEMBER_ME, false);
    }

    /**
     * setRememberMe
     *
     * @param rememberMe
     */
    public void setRememberMe(boolean rememberMe) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, PreferenceDef.CLOUD_REMEMBER_ME, rememberMe);
    }

    /**
     * 退出登录
     * 清除all SP
     */
    public void clearAllSP() {
        mPreferenceDataRepository.clear(PreferenceDef.SP_NAME_USER_COOKIE);
    }

    /**
     * 退出制定的SP
     */
    public void clearSPByName(String SP) {
        mPreferenceDataRepository.clear(SP);
    }

    /**
     * 保存wifi信息
     *
     * @param ssid     账号
     * @param password 密码
     */
    public void setWifiPassword(String ssid, String password) {
        mPreferenceDataRepository.put(PreferenceDef.SP_NAME_USER_COOKIE, ssid, password);
    }

    public String getWifiPassword(String ssid) {
        return (String) mPreferenceDataRepository.get(PreferenceDef.SP_NAME_USER_COOKIE, ssid, "");
    }

    /**
     * save new version flag
     *
     * @param hasNewVersion flag
     */
    public void setHasNewVersion(boolean hasNewVersion) {
        mPreferenceDataRepository.put(PreferenceDef.VERSION_CONFIG, PreferenceDef.APK_HAS_NEW_VERSION, hasNewVersion);
    }

    /**
     * get new version flag
     */
    public boolean isHasNewVersion() {
        return (boolean) mPreferenceDataRepository.get(PreferenceDef.VERSION_CONFIG, PreferenceDef.APK_HAS_NEW_VERSION, false);
    }

    /******************************SharedPreference Repository end****************************/


    /*********************************File Repository Start***********************************/


    /*********************************File Repository end*************************************/

}
