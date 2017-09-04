package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.device.BindDeviceBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by feilong.yang on 2017/7/14.
 */

public interface HttpDeviceInfoInterface {

    @POST("plug/bindDev")
    Observable<BindDeviceBean> binddev(@Header("Authorization") String access_token,
                                       @Query("mac") String mac);

    @POST("unbindDev")
    Observable<DeviceResultBean> unBinddev(@Header("Authorization") String access_token,
                                           @Query("deviceID") String deviceId);

    @POST("rename")
    Observable<DeviceResultBean> modifyDeviceName(@Header("Authorization") String access_token,
                                                  @Query("deviceID") String deviceID,
                                                  @Query("name") String newName);

    @GET("getBindDevs")
    Observable<DeviceListsBean> getBindDevs(@Header("Authorization") String access_token,
                                            @Query("productID") String productID);


    @POST("setAttributes")
    Observable<DeviceResultBean> modifyDevice(@Header("Authorization") String access_token,
                                              @Query("attributes") String attributes,
                                              @Query("deviceID") String deviceId);

}
