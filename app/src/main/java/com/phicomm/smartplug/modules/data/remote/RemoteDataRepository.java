package com.phicomm.smartplug.modules.data.remote;


import android.content.Context;

import com.phicomm.smartplug.modules.data.remote.http.HttpRemoteDataRepository;

import java.io.File;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscription;


/**
 * Created by wenhui02.liu on 2016/11/3.
 */
public class RemoteDataRepository {

    private static final String TAG = "RemoteDataRepository";
    private HttpRemoteDataRepository mHttpRemoteDataRepository;

    public RemoteDataRepository(Context context) {
        mHttpRemoteDataRepository = new HttpRemoteDataRepository();
    }

    /******************************************HTTP Remote Data**************************************/
    /**
     * getAuthorization
     *
     * @param subscriber
     * @param map
     */
    public Subscription getAuthorization(Object liftcycleObj,
                                         Observer subscriber,
                                         Map<String, String> map) {
        return mHttpRemoteDataRepository.getAuthorization(liftcycleObj, subscriber, map);
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
        return mHttpRemoteDataRepository.verifyCode(liftcycleObj, subscriber, map);
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
        return mHttpRemoteDataRepository.captcha(liftcycleObj, subscriber, map);
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
        return mHttpRemoteDataRepository.checkPhoneNumber(liftcycleObj, subscriber, map);
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
        return mHttpRemoteDataRepository.login(liftcycleObj, subscriber, body);
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
        return mHttpRemoteDataRepository.register(liftcycleObj, subscriber, body);
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
        return mHttpRemoteDataRepository.forgetPassword(liftcycleObj, subscriber, body);
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
        return mHttpRemoteDataRepository.refreshToken(liftcycleObj, subscriber, refreshToken, map);
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
        return mHttpRemoteDataRepository.accountDetail(liftcycleObj, subscriber, accessToken);
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
        return mHttpRemoteDataRepository.modifyPassword(liftcycleObj, subscriber, accessToken, body);
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
        return mHttpRemoteDataRepository.modifyProperty(liftcycleObj, subscriber, accessToken, body);
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
        return mHttpRemoteDataRepository.uploadAvatar(liftcycleObj, subscriber, accessToken, file, type);
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
        return mHttpRemoteDataRepository.checkNewVersion(subscriber, appid, channel, verCode);
    }

    /*add by yangfeilong*/
    public Subscription bindDevice(Object object, Observer subscriber, String access_token, String mac) {
        return mHttpRemoteDataRepository.bindDevice(object, subscriber, access_token, mac);
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
        return mHttpRemoteDataRepository.unBindDevice(object, subscriber, access_token, deviceId);
    }

    public Subscription modifyDeviceName(Object object, Observer subscriber, String access_token, String deviceID, String newName) {
        return mHttpRemoteDataRepository.modifyDeviceName(object, subscriber, access_token, deviceID, newName);
    }

    public Subscription getBindDevs(Object object, Observer subscriber, String access_token, String productID) {
        return mHttpRemoteDataRepository.getBindDevs(object, subscriber, access_token, productID);
    }

    /**
     * 获取设备状态
     *
     * @param subscriber
     */
    public Subscription getDeviceStatus(Object object, Observer subscriber, String access_token, String deviceId) {
        return mHttpRemoteDataRepository.getDeviceStatus(object, subscriber, access_token, deviceId);
    }


    /**
     * 设备控制
     *
     * @param subscriber
     * @param body
     */
    public Subscription getDeviceControl(Object object, String access_token, Observer subscriber, RequestBody body, String deviceId) {
        return mHttpRemoteDataRepository.getDeviceControl(object, access_token, subscriber, body, deviceId);
    }

    /**
     * 修改设备插口的名称
     *
     * @param subscriber
     * @param access_token
     * @param attributes
     * @param deviceId
     * @return
     */
    public Subscription modifyDevice(Object object, Observer subscriber, String access_token, String attributes, String deviceId) {
        return mHttpRemoteDataRepository.modifyDevice(object, subscriber, access_token, attributes, deviceId);
    }


    /**
     * 获取设备月消耗电量
     *
     * @param subscriber
     */
    public Subscription dayElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour,
                                 String day_start_hour, String end_day,
                                 String start_day) {
        return mHttpRemoteDataRepository.dayElect(object, subscriber, access_token, deviceId, day_end_hour, day_start_hour, end_day, start_day);
    }

    /**
     * 获取设备累计用电总量
     *
     * @param subscriber
     */
    public Subscription totalElect(Object object, String access_token, Observer subscriber, String deviceId) {
        return mHttpRemoteDataRepository.totalElect(object, access_token, subscriber, deviceId);
    }

    /**
     * 获取设备年消耗电量
     *
     * @param subscriber
     */
    public Subscription monthElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour, String day_start_hour,
                                   String end_year_month, String start_year_month) {
        return mHttpRemoteDataRepository.monthElect(object, subscriber, access_token, deviceId, day_end_hour, day_start_hour, end_year_month, start_year_month);
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
        return mHttpRemoteDataRepository.createScene(lifeCycleObj, subscriber, access_token, json);
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
        return mHttpRemoteDataRepository.getSceneList(lifeCycleObj, subscriber, access_token, productID);
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
        return mHttpRemoteDataRepository.getSceneDetail(lifeCycleObj, subscriber, access_token, sceneId);
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
        return mHttpRemoteDataRepository.editScene(lifeCycleObj, subscriber, access_token, json);
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
        return mHttpRemoteDataRepository.deleteScene(lifeCycleObj, subscriber, access_token, sceneId);
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
        return mHttpRemoteDataRepository.executeScene(lifeCycleObj, subscriber, access_token, sceneId);
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
        return mHttpRemoteDataRepository.cancelScene(lifeCycleObj, subscriber, access_token, sceneId);
    }

}
