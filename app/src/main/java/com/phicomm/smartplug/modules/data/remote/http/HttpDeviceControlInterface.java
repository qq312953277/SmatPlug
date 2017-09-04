package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by feilong.yang on 2017/7/14.
 */

public interface HttpDeviceControlInterface {

    @GET("{deviceId}/totalElect")
    Observable<DeviceStatusBean> totalElect(@Header("Authorization") String access_token, @Path("deviceId") String device_id);

    @GET("{deviceId}/dayElect")
    Observable<DeviceStatusBean> dayElect(@Header("Authorization") String access_token, @Path("deviceId") String device_id, @Query("day_end_hour") String day_end_hour,
                                          @Query("day_start_hour") String day_start_hour, @Query("end_day") String end_day,
                                          @Query("start_day") String start_day);

    @GET("{deviceId}/monthElect")
    Observable<DeviceStatusBean> monthElect(@Header("Authorization") String access_token, @Path("deviceId") String device_id, @Query("day_end_hour") String day_end_hour,
                                            @Query("day_start_hour") String day_start_hour, @Query("end_year_month") String end_year_month,
                                            @Query("start_year_month") String start_year_month);


    @GET("{deviceId}/online")
    Observable<DeviceStatusBean> getDeviceStatus(@Header("Authorization") String access_token, @Path("deviceId") String device_id);

    @Headers("Content-Type:application/json")
    @POST("{deviceId}/command")
    Observable<DeviceStatusBean> getDeviceControl(@Header("Authorization") String access_token, @Path("deviceId") String device_id, @Body RequestBody body);
}
