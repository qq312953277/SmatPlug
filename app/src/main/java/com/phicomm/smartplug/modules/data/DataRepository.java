package com.phicomm.smartplug.modules.data;

import android.content.Context;
import android.util.Log;

import com.phicomm.smartplug.modules.data.local.LocalDataRepository;
import com.phicomm.smartplug.modules.data.remote.RemoteDataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.utils.AESEncryptorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscription;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public class DataRepository {

    public static final String TAG = "DataRepository";
    private static DataRepository INSTANCE = null;

    private LocalDataRepository mLocalDataRepository;
    private RemoteDataRepository mRemoteDataRepository;
    private ArrayList<DataModifiedListener> mDataModifiedListener;

    private DataRepository(Context context) {
        mLocalDataRepository = new LocalDataRepository(context);
        mRemoteDataRepository = new RemoteDataRepository(context);
        mDataModifiedListener = new ArrayList<DataModifiedListener>();
    }

    public void resetLocalDateBase() {
        mLocalDataRepository.resetLocalDateBase();
    }

    public static DataRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(context);
        }
        return INSTANCE;
    }

    public static DataRepository getInstance() {
        return INSTANCE;
    }

    /******************************************Local Data**************************************/
    public LocalDataRepository getLocalDataRepo() {
        return mLocalDataRepository;
    }

    /**
     * Add a listener {@link DataModifiedListener} for receiving data changes from {@link DataModifiedListener}.
     *
     * @param listener {@link DataModifiedListener}
     */
    public void registerDatabaseListener(DataModifiedListener listener) {
        if (!mDataModifiedListener.contains(listener)) {
            mDataModifiedListener.add(listener);
            Log.d(TAG, "registerDatabaseListener num = " + mDataModifiedListener.size());
        }
    }

    /**
     * Remove a listener {@link DataModifiedListener} from {@link DataModifiedListener}.
     *
     * @param listener {@link DataModifiedListener}
     */
    public void unregisterDatabaseListener(DataModifiedListener listener) {
        mDataModifiedListener.remove(listener);
    }

    private void notifySettingsDataModified(String name, String value) {
        for (int i = 0; i < mDataModifiedListener.size(); i++) {
            mDataModifiedListener.get(i).onSettingsDataModified(name, value);
        }
    }

    private void notifyTrainListAdded(String trainId) {
        for (int i = 0; i < mDataModifiedListener.size(); i++) {
            mDataModifiedListener.get(i).onTrainListAdded(trainId);
        }
    }

    private void notifyRunListModified() {
        for (int i = 0; i < mDataModifiedListener.size(); i++) {
            mDataModifiedListener.get(i).onRunListModified();
        }
    }

    private void notifyPlanStateModified(String planId) {
        for (int i = 0; i < mDataModifiedListener.size(); i++) {
            mDataModifiedListener.get(i).onPlanStateModified(planId);
        }
    }

    /******************Database begin***********/

    /**
     * 增
     *
     * @param deviceBean
     */
    public void insert(DeviceListsBean.DeviceBean deviceBean) {
        mLocalDataRepository.insert(deviceBean);
    }

    /**
     * 更新设备
     *
     * @param deviceBean
     */
    public void updateDevice(DeviceListsBean.DeviceBean deviceBean) {
        mLocalDataRepository.updateDevice(deviceBean);
    }

    /**
     * 删
     *
     * @param deviceId
     */
    public void delete(String deviceId) {
        mLocalDataRepository.delete(deviceId);
    }

    public void deleteAll() {
        mLocalDataRepository.deleteAll();
    }

    /**
     * 修改设备名称
     *
     * @param deviceId
     * @param deviceName
     */
    public void update(String deviceId, String deviceName) {
        mLocalDataRepository.update(deviceId, deviceName);
    }

    /**
     * 修改设备插口名称
     *
     * @param bean
     */
    public void updateDeviceSwitchName(DeviceResultBean bean) {
        mLocalDataRepository.updateDeviceSwitchName(bean);
    }

    /**
     * 查
     *
     * @return
     */
    public ArrayList<DeviceListsBean.DeviceBean> query() {
        return mLocalDataRepository.query();
    }
    /******************Database end***********/

    /**
     * 获取授权码
     */
    public String getAuthorizationCode() {
        return AESEncryptorUtils.decrypt(mLocalDataRepository.getAuthorizationCode());
    }

    /**
     * 设置授权码
     */
    public void setAuthorizationCode(String code) {
        mLocalDataRepository.setAuthorizationCode(AESEncryptorUtils.encrypt(code));
    }

    /**
     * app是否引导过
     *
     * @return
     */
    public boolean isAppGuided() {
        return mLocalDataRepository.isAppGuided();
    }

    /**
     * 记录下app已经被用户引导过了
     */
    public void setAppGuided(boolean isguided) {
        mLocalDataRepository.setAppGuided(isguided);
    }

    /**
     * 云端之前是否已经登录了
     *
     * @return 登录状态
     */
    public boolean isCloudLogined() {
        return mLocalDataRepository.isCloudLogined();
    }

    /**
     * 设置云端的登录状态
     *
     * @param loginStatus 已登录为true，已登出为false
     */
    public void setCloudLoginStatus(boolean loginStatus) {
        mLocalDataRepository.setCloudLoginStatus(loginStatus);
    }

    /**
     * 设置用户的基本信息
     */
    public void setAccountDetailInfo(AccountDetailBean accountDetailBean) {
        mLocalDataRepository.setAccountDetailInfo(accountDetailBean);
    }

    /**
     * 获取用户的基本信息
     *
     * @return
     */
    public AccountDetailBean getAccountDetailInfo() {
        return (AccountDetailBean) mLocalDataRepository.getAccountDetailInfo();
    }

    /**
     * getAccessToken
     *
     * @return
     */
    public String getAccessToken() {
        return (String) AESEncryptorUtils.decrypt(mLocalDataRepository.getAccessToken());
    }

    /**
     * setAccessToken
     */
    public void setAccessToken(String accessToken) {
        mLocalDataRepository.setAccessToken(AESEncryptorUtils.encrypt(accessToken));
    }

    /**
     * getRefreshToken
     *
     * @return
     */
    public String getRefreshToken() {
        return (String) AESEncryptorUtils.decrypt(mLocalDataRepository.getRefreshToken());
    }

    /**
     * setRefreshToken
     *
     * @param refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        mLocalDataRepository.setRefreshToken(AESEncryptorUtils.encrypt(refreshToken));
    }

    /**
     * getAccessValidity
     *
     * @return
     */
    public Long getAccessValidity() {
        return (Long) mLocalDataRepository.getAccessValidity();
    }

    /**
     * setAccessValidity
     *
     * @param accessValidity
     */
    public void setAccessValidity(Long accessValidity) {
        mLocalDataRepository.setAccessValidity(accessValidity);
    }

    /**
     * getRefreshValidity
     *
     * @return
     */
    public Long getRefreshValidity() {
        return (Long) mLocalDataRepository.getRefreshValidity();
    }

    /**
     * setRefreshValidity
     *
     * @param refreshValidity
     */
    public void setRefreshValidity(Long refreshValidity) {
        mLocalDataRepository.setRefreshValidity(refreshValidity);
    }

    /**
     * getRefreshStartTime
     *
     * @return
     */
    public Long getRefreshStartTime() {
        return (Long) mLocalDataRepository.getRefreshStartTime();
    }

    /**
     * setRefreshStartTime
     *
     * @param refreshStartTime
     */
    public void setRefreshStartTime(Long refreshStartTime) {
        mLocalDataRepository.setRefreshStartTime(refreshStartTime);
    }

    /**
     * V
     *
     * @return
     */
    public Long getAccessStartTime() {
        return (Long) mLocalDataRepository.getAccessStartTime();
    }

    /**
     * setAccessStartTime
     *
     * @param accessStartTime
     */
    public void setAccessStartTime(Long accessStartTime) {
        mLocalDataRepository.setAccessStartTime(accessStartTime);
    }

    /**
     * getUserName
     */
    public String getUserName() {
        return AESEncryptorUtils.decrypt(mLocalDataRepository.getUserName());
    }

    /**
     * setUsername
     *
     * @param username
     */
    public void setUsername(String username) {
        mLocalDataRepository.setUsername(AESEncryptorUtils.encrypt(username));
    }

    /**
     * getPassword
     */
    public String getPassword() {
        return AESEncryptorUtils.decrypt(mLocalDataRepository.getPassword());
    }

    /**
     * setPassword
     *
     * @param password
     */
    public void setPassword(String password) {
        mLocalDataRepository.setPassword(AESEncryptorUtils.encrypt(password));
    }

    /**
     * getRememberMe
     *
     * @return
     */
    public boolean getRememberMe() {
        return mLocalDataRepository.getRememberMe();
    }

    /**
     * setRememberMe
     *
     * @param rememberMe
     */
    public void setRememberMe(boolean rememberMe) {
        mLocalDataRepository.setRememberMe(rememberMe);
    }

    /**
     * 退出登录
     * 清除all SP
     */
    public void clearAllSP() {
        mLocalDataRepository.clearAllSP();
    }

    /**
     * 退出制定的SP
     */
    public void clearSPByName(String SP) {
        mLocalDataRepository.clearSPByName(SP);
    }

    /**
     * 保存wifi信息
     *
     * @param ssid     账号
     * @param password 密码
     */
    public void setWifiPassword(String ssid, String password) {
        mLocalDataRepository.setWifiPassword(ssid, password);
    }

    public String getWifiPassword(String ssid) {
        return mLocalDataRepository.getWifiPassword(ssid);
    }
    /******************************************Remote Data**************************************/

    /**
     * getAuthorization
     *
     * @param subscriber
     * @param map
     */
    public Subscription getAuthorization(Object liftcycleObj,
                                         Observer subscriber,
                                         Map<String, String> map) {
        return mRemoteDataRepository.getAuthorization(liftcycleObj, subscriber, map);
    }

    /**
     * verifyCode
     *
     * @param subscriber
     * @param map
     */
    public Subscription verifyCode(Object liftcycleObj,
                                   Observer subscriber,
                                   Map<String, String> map) {
        return mRemoteDataRepository.verifyCode(liftcycleObj, subscriber, map);
    }

    /**
     * captcha
     *
     * @param subscriber
     * @param map
     */
    public Subscription captcha(Object liftcycleObj,
                                Observer subscriber,
                                Map<String, String> map) {
        return mRemoteDataRepository.captcha(liftcycleObj, subscriber, map);
    }

    /**
     * checkPhoneNumber
     *
     * @param subscriber
     * @param map
     */
    public Subscription checkPhoneNumber(Object liftcycleObj,
                                         Observer subscriber,
                                         Map<String, String> map) {
        return mRemoteDataRepository.checkPhoneNumber(liftcycleObj, subscriber, map);
    }

    /**
     * login
     *
     * @param subscriber
     * @param body
     */
    public Subscription login(Object liftcycleObj,
                              Observer subscriber,
                              RequestBody body) {
        return mRemoteDataRepository.login(liftcycleObj, subscriber, body);
    }

    /**
     * register
     *
     * @param subscriber
     * @param body
     */
    public Subscription register(Object liftcycleObj,
                                 Observer subscriber,
                                 RequestBody body) {
        return mRemoteDataRepository.register(liftcycleObj, subscriber, body);
    }

    /**
     * forgetPassword
     *
     * @param subscriber
     * @param body
     */
    public Subscription forgetPassword(Object liftcycleObj,
                                       Observer subscriber,
                                       RequestBody body) {
        return mRemoteDataRepository.forgetPassword(liftcycleObj, subscriber, body);
    }

    /**
     * refreshToken
     *
     * @param subscriber
     * @param map
     */
    public Subscription refreshToken(Object liftcycleObj,
                                     Observer subscriber,
                                     String refreshToken,
                                     Map<String, String> map) {
        return mRemoteDataRepository.refreshToken(liftcycleObj, subscriber, refreshToken, map);
    }

    /**
     * accountDetail
     *
     * @param subscriber
     * @param accessToken
     */
    public Subscription accountDetail(Object liftcycleObj,
                                      Observer subscriber,
                                      String accessToken) {
        return mRemoteDataRepository.accountDetail(liftcycleObj, subscriber, accessToken);
    }

    /**
     * modifyPassword
     *
     * @param subscriber
     * @param body
     */
    public Subscription modifyPassword(Object liftcycleObj,
                                       Observer subscriber,
                                       String accessToken,
                                       RequestBody body) {
        return mRemoteDataRepository.modifyPassword(liftcycleObj, subscriber, accessToken, body);
    }

    /**
     * modifyProperty
     *
     * @param subscriber
     * @param accessToken
     * @param body
     */
    public Subscription modifyProperty(Object liftcycleObj,
                                       Observer subscriber,
                                       String accessToken,
                                       RequestBody body) {
        return mRemoteDataRepository.modifyProperty(liftcycleObj, subscriber, accessToken, body);
    }

    /**
     * @param subscriber
     * @param accessToken
     * @param file
     * @param type
     */
    public Subscription uploadAvatar(Object liftcycleObj,
                                     Observer subscriber,
                                     String accessToken, File file, String type) {
        return mRemoteDataRepository.uploadAvatar(liftcycleObj, subscriber, accessToken, file, type);
    }

    /**
     * checkNewVersion
     *
     * @param subscriber
     * @param appid
     * @param channel
     * @param verCode
     */
    public Subscription checkNewVersion(Observer subscriber,
                                        String appid, String channel, int verCode) {
        return mRemoteDataRepository.checkNewVersion(subscriber, appid, channel, verCode);
    }

    public void setHasNewVersion(boolean hasNewVersion) {
        mLocalDataRepository.setHasNewVersion(hasNewVersion);
    }

    public boolean isHasNewVersion() {
        return mLocalDataRepository.isHasNewVersion();
    }

    /*add by yangfeilong*/
    public Subscription bindDevice(Object object, Observer subscriber, String access_token, String mac) {
        return mRemoteDataRepository.bindDevice(object, subscriber, access_token, mac);
    }

    /**
     * 设备解除绑定
     *
     * @param subscriber
     * @param access_token
     * @param deviceId
     * @return
     */
    public Subscription unBindDevice(Object object, Observer subscriber, String access_token, String deviceId) {
        return mRemoteDataRepository.unBindDevice(object, subscriber, access_token, deviceId);
    }

    public Subscription modifyDeviceName(Object object, Observer subscriber, String access_token, String deviceID, String newName) {
        return mRemoteDataRepository.modifyDeviceName(object, subscriber, access_token, deviceID, newName);
    }

    public Subscription getBindDevs(Object object, Observer subscriber, String access_token, String productID) {
        return mRemoteDataRepository.getBindDevs(object, subscriber, access_token, productID);
    }

    /**
     * 获取设备状态
     *
     * @param subscriber
     */
    public Subscription getDeviceStatus(Object object, Observer subscriber, String access_token, String deviceId) {
        return mRemoteDataRepository.getDeviceStatus(object, subscriber, access_token, deviceId);
    }


    /**
     * 设备控制
     *
     * @param subscriber
     * @param body
     */
    public Subscription getDeviceControl(Object object, String access_token, Observer subscriber, RequestBody body, String deviceId) {
        return mRemoteDataRepository.getDeviceControl(object, access_token, subscriber, body, deviceId);
    }

    /**
     * 修改设备插口的名称
     *
     * @param subscriber
     */
    public Subscription modifyDevice(Object object, Observer subscriber, String access_token, String attributes, String deviceId) {
        return mRemoteDataRepository.modifyDevice(object, subscriber, access_token, attributes, deviceId);
    }

    /**
     * 获取设备月消耗电量
     *
     * @param subscriber
     */
    public Subscription dayElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour,
                                 String day_start_hour, String end_day,
                                 String start_day) {
        return mRemoteDataRepository.dayElect(object, subscriber, access_token, deviceId, day_end_hour, day_start_hour, end_day, start_day);
    }

    /**
     * 获取设备累计用电总量
     *
     * @param subscriber
     */
    public Subscription totalElect(Object object, String access_token, Observer subscriber, String deviceId) {
        return mRemoteDataRepository.totalElect(object, access_token, subscriber, deviceId);
    }

    /**
     * 获取设备年消耗电量
     *
     * @param subscriber
     */
    public Subscription monthElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour,
                                   String day_start_hour, String end_year_month,
                                   String start_year_month) {
        return mRemoteDataRepository.monthElect(object, subscriber, access_token, deviceId, day_end_hour, day_start_hour, end_year_month, start_year_month);
    }

    // scene

    /**
     * create scene
     *
     * @param subscriber
     * @param access_token
     * @param json
     */
    public Subscription createScene(Object lifeCycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    String json) {
        return mRemoteDataRepository.createScene(lifeCycleObj, subscriber, access_token, json);
    }

    /**
     * get scene list
     *
     * @param subscriber
     * @param access_token
     * @param productID
     */
    public Subscription getSceneList(Object lifeCycleObj,
                                     Observer subscriber,
                                     String access_token,
                                     String productID) {
        return mRemoteDataRepository.getSceneList(lifeCycleObj, subscriber, access_token, productID);
    }

    /**
     * get scene detail
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription getSceneDetail(Object lifeCycleObj,
                                       Observer subscriber,
                                       String access_token,
                                       long sceneId) {
        return mRemoteDataRepository.getSceneDetail(lifeCycleObj, subscriber, access_token, sceneId);
    }

    /**
     * edit scene
     *
     * @param subscriber
     * @param access_token
     * @param json
     */
    public Subscription editScene(Object lifeCycleObj,
                                  Observer subscriber,
                                  String access_token,
                                  String json) {
        return mRemoteDataRepository.editScene(lifeCycleObj, subscriber, access_token, json);
    }

    /**
     * delete scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription deleteScene(Object lifeCycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    long sceneId) {
        return mRemoteDataRepository.deleteScene(lifeCycleObj, subscriber, access_token, sceneId);
    }

    /**
     * execute scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription executeScene(Object lifeCycleObj,
                                     Observer subscriber,
                                     String access_token,
                                     long sceneId) {
        return mRemoteDataRepository.executeScene(lifeCycleObj, subscriber, access_token, sceneId);
    }

    /**
     * cancel scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription cancelScene(Object lifeCycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    long sceneId) {
        return mRemoteDataRepository.cancelScene(lifeCycleObj, subscriber, access_token, sceneId);
    }
}
