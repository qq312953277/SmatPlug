package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.utils.LogUtils;
import com.trello.rxlifecycle.components.RxActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpRemoteDataRepository {

    private static final String TAG = "HttpRemoteData";
    private static final int DEFAULT_TIMEOUT = 15;
    private Retrofit phicommCommonRetrofit;
    private Retrofit phicommAvatarRetrofit;
    private Retrofit versionUpdateRetrofit;
    private HttpCloudInterface myHttpApiInterface;
    private HttpAvatarInterface avaterHttpApiInterface;
    private HttpVersionInterface versionUpdateHttpApiInterface;

    // scene interface
    private Retrofit sceneRetrofit;
    private HttpSceneInterface sceneHttpApiInterface;

    /*add by yangfeilong*/
    private HttpDeviceInfoInterface deviceInfoInterface;
    private HttpDeviceControlInterface deviceControlInterface;
    private Retrofit deviceInfoRetrofit;
    private Retrofit deviceControlRetrofit;

    //http log
    class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            LogUtils.d("OkHttpLogInfo", message);
        }
    }

    private Retrofit getRetrofit(String baseUrl, String retrofitId) {
        //log
        HttpLoggingInterceptor okhttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        okhttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //https cer
        HttpsCerUtils.SSLParams sslParams = HttpsCerUtils.getSslSocketFactory(null, null, null);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(okhttpLoggingInterceptor) //log
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        return new Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public HttpRemoteDataRepository() {
        phicommCommonRetrofit = getRetrofit(HttpConfig.PHICOMM_CLOUD_BASE_URL, "0");
        myHttpApiInterface = phicommCommonRetrofit.create(HttpCloudInterface.class);

        phicommAvatarRetrofit = getRetrofit(HttpConfig.PHICOMM_AVATER_BASE_URL, "0");
        avaterHttpApiInterface = phicommAvatarRetrofit.create(HttpAvatarInterface.class);

        versionUpdateRetrofit = getRetrofit(HttpConfig.PHICOMM_OTA_BASE_URL, "0");
        versionUpdateHttpApiInterface = versionUpdateRetrofit.create(HttpVersionInterface.class);

        /*add by yangfeilong*/
        deviceInfoRetrofit = getRetrofit(HttpConfig.PHICLOUDS_BASE_TEST_URL, "0");
        deviceInfoInterface = deviceInfoRetrofit.create(HttpDeviceInfoInterface.class);

        deviceControlRetrofit = getRetrofit(HttpConfig.PHICLOUDS_DEVICE_CONTROL_URL, "0");
        deviceControlInterface = deviceControlRetrofit.create(HttpDeviceControlInterface.class);

        // scene
        sceneRetrofit = getRetrofit(HttpConfig.PHICOMM_SCENE_BASE_URL, "0");
        sceneHttpApiInterface = sceneRetrofit.create(HttpSceneInterface.class);
    }

    public <T> Observable.Transformer<? super T, ?> getLifeCycleObj(Object liftcycleObj, Class<T> t) {
        if (liftcycleObj instanceof RxActivity) {
            RxActivity rxObj = (RxActivity) liftcycleObj;
            return ((rxObj.<T>bindToLifecycle()));

        } else if (liftcycleObj instanceof RxFragmentActivity) {
            RxFragmentActivity rxObj = (RxFragmentActivity) liftcycleObj;
            return ((rxObj.<T>bindToLifecycle()));

        } else if (liftcycleObj instanceof RxFragment) {
            RxFragment rxObj = (RxFragment) liftcycleObj;
            return ((rxObj.<T>bindToLifecycle()));

        } else if (liftcycleObj instanceof RxAppCompatActivity) {
            RxAppCompatActivity rxObj = (RxAppCompatActivity) liftcycleObj;
            return ((rxObj.<T>bindToLifecycle()));

        }
        return null;
    }

    /**
     * getAuthorization
     *
     * @param subscriber
     * @param map
     */
    public Subscription getAuthorization(@NotNull Object liftcycleObj,
                                         Observer subscriber,
                                         Map<String, String> map) {
        return myHttpApiInterface.authorization(map)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * verifyCode
     *
     * @param subscriber
     * @param map
     */
    public Subscription verifyCode(@NotNull Object liftcycleObj,
                                   Observer subscriber,
                                   Map<String, String> map) {
        return myHttpApiInterface.verifyCodeCaptcha(map)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * captcha
     *
     * @param subscriber
     * @param map
     */
    public Subscription captcha(@NotNull Object liftcycleObj,
                                Observer subscriber,
                                Map<String, String> map) {
        return myHttpApiInterface.captcha(map)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * checkPhoneNumber
     *
     * @param subscriber
     * @param map
     */
    public Subscription checkPhoneNumber(@NotNull Object liftcycleObj,
                                         Observer subscriber,
                                         Map<String, String> map) {
        return myHttpApiInterface.checkPhonenumber(map)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))/*生命周期管理 整个请求通过compose设定的rxlifecycle来管理生命周期，所以不会溢出和泄露无需任何担心*/
                .subscribeOn(Schedulers.io()) /*http请求线程*/
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())/*回调线程*/
                .subscribe(subscriber);
    }

    /**
     * login
     *
     * @param subscriber
     * @param body
     */
    public Subscription login(@NotNull Object liftcycleObj,
                              Observer subscriber,
                              RequestBody body) {
        return myHttpApiInterface.login(body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * register
     *
     * @param subscriber
     * @param body
     */
    public Subscription register(@NotNull Object liftcycleObj,
                                 Observer subscriber,
                                 RequestBody body) {
        return myHttpApiInterface.register(body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * forgetPassword
     *
     * @param subscriber
     * @param body
     */
    public Subscription forgetPassword(@NotNull Object liftcycleObj,
                                       Observer subscriber,
                                       RequestBody body) {
        return myHttpApiInterface.forgetPassword(body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * refreshToken
     *
     * @param subscriber
     * @param map
     */
    public Subscription refreshToken(@NotNull Object liftcycleObj,
                                     Observer subscriber,
                                     String refreshToken,
                                     Map<String, String> map) {
        return myHttpApiInterface.refreshToken(refreshToken, map)
//                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class)) //refresh token neednot liftcycleobj
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * accountDetail
     *
     * @param subscriber
     * @param accessToken
     */
    public Subscription accountDetail(@NotNull Object liftcycleObj,
                                      Observer subscriber,
                                      String accessToken) {
        return myHttpApiInterface.accountDetail(accessToken)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * modifyPassword
     *
     * @param subscriber
     * @param body
     */
    public Subscription modifyPassword(@NotNull Object liftcycleObj,
                                       Observer subscriber,
                                       String accessToken,
                                       RequestBody body) {
        return myHttpApiInterface.modifyPassword(accessToken, body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * modifyProperty
     *
     * @param subscriber
     * @param accessToken
     * @param body
     */
    public Subscription modifyProperty(@NotNull Object liftcycleObj,
                                       Observer subscriber,
                                       String accessToken,
                                       RequestBody body) {
        return myHttpApiInterface.modifyProperty(accessToken, body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * @param subscriber
     * @param accessToken
     * @param file
     * @param type
     */
    public Subscription uploadAvatar(@NotNull Object liftcycleObj,
                                     Observer subscriber,
                                     String accessToken, File file, String type) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody typeBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), type);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("type", typeBody);
        return avaterHttpApiInterface.uploadAvatar(accessToken, filePart, map)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * checkNewVersion
     *
     * @param subscriber
     * @param appid
     * @param channel
     * @param verCode
     */
    public Subscription checkNewVersion(Observer subscriber, String appid, String channel, int verCode) {
        return versionUpdateHttpApiInterface.checkNewVersion(appid, channel, verCode)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    public Subscription bindDevice(Object object, Observer subscriber, String access_token, String mac) {
        return deviceInfoInterface.binddev(access_token, mac)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Subscription unBindDevice(Object object, Observer subscriber, String access_token, String deviceId) {
        return deviceInfoInterface.unBinddev(access_token, deviceId)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Subscription modifyDeviceName(Object object, Observer subscriber, String access_token, String deviceID, String newName) {
        return deviceInfoInterface.modifyDeviceName(access_token, deviceID, newName)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Subscription getBindDevs(Object object, Observer subscriber, String access_token, String productID) {
        return deviceInfoInterface.getBindDevs(access_token, productID)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取设备状态
     *
     * @param subscriber
     */
    public Subscription getDeviceStatus(Object object, Observer subscriber, String access_token, String deviceId) {
        return deviceControlInterface.getDeviceStatus(access_token, deviceId)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * 设备控制
     *
     * @param subscriber
     * @param body
     */
    public Subscription getDeviceControl(Object object, String access_token, Observer subscriber, RequestBody body, String deviceId) {
        return deviceControlInterface.getDeviceControl(access_token, deviceId, body)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取设备累计用电总量
     *
     * @param subscriber
     */
    public Subscription totalElect(Object object, String access_token, Observer subscriber, String deviceId) {
        return deviceControlInterface.totalElect(access_token, deviceId)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取设备月消耗电量
     *
     * @param subscriber
     */
    public Subscription dayElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour,
                                 String day_start_hour, String end_day,
                                 String start_day) {
        return deviceControlInterface.dayElect(access_token, deviceId, "", "", "", "")
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取设备年消耗电量
     *
     * @param subscriber
     */
    public Subscription monthElect(Object object, Observer subscriber, String access_token, String deviceId, String day_end_hour,
                                   String day_start_hour, String end_year_month,
                                   String start_year_month) {
        return deviceControlInterface.monthElect(access_token, deviceId, "", "", "", "")
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 修改设备插口的名字
     *
     * @param subscriber
     */
    public Subscription modifyDevice(Object object, Observer subscriber, String access_token, String attributes, String deviceId) {
        return deviceInfoInterface.modifyDevice(access_token, attributes, deviceId)
                .compose(getLifeCycleObj(object, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * create scene
     *
     * @param subscriber
     * @param access_token
     * @param json
     */
    public Subscription createScene(@NotNull Object liftcycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    String json) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        return sceneHttpApiInterface.createScene(access_token, body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * get scene list
     *
     * @param subscriber
     * @param access_token
     * @param productID
     */
    public Subscription getSceneList(@NotNull Object liftcycleObj,
                                     Observer subscriber,
                                     String access_token,
                                     String productID) {
        return sceneHttpApiInterface.getSceneList(access_token, productID)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * get scene detail
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription getSceneDetail(@NotNull Object liftcycleObj,
                                       Observer subscriber,
                                       String access_token,
                                       long sceneId) {
        return sceneHttpApiInterface.getSceneDetail(access_token, sceneId)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * edit scene
     *
     * @param subscriber
     * @param access_token
     * @param json
     */
    public Subscription editScene(@NotNull Object liftcycleObj,
                                  Observer subscriber,
                                  String access_token,
                                  String json) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        return sceneHttpApiInterface.editScene(access_token, body)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * delete scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription deleteScene(@NotNull Object liftcycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    long sceneId) {
        return sceneHttpApiInterface.deleteScene(access_token, sceneId)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * execute scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription executeScene(@NotNull Object liftcycleObj,
                                     Observer subscriber,
                                     String access_token,
                                     long sceneId) {
        return sceneHttpApiInterface.executeScene(access_token, sceneId)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * cancel scene
     *
     * @param subscriber
     * @param access_token
     * @param sceneId
     */
    public Subscription cancelScene(@NotNull Object liftcycleObj,
                                    Observer subscriber,
                                    String access_token,
                                    long sceneId) {
        return sceneHttpApiInterface.cancelScene(access_token, sceneId)
                .compose(getLifeCycleObj(liftcycleObj, BaseResponseBean.class))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
