package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.update.CheckVersionResponseBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpVersionInterface {
    @GET("Service/App/checkupdate")
    Observable<CheckVersionResponseBean> checkNewVersion(@Query("appid") String appid,
                                                         @Query("channel") String channel,
                                                         @Query("vercode") int versionCode);
}
