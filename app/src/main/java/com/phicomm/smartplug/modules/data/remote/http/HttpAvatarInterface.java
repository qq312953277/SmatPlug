package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.account.UploadAvatarBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public interface HttpAvatarInterface {

    @Multipart
    @POST("upload")
    Observable<UploadAvatarBean> uploadAvatar(@Header("Authorization") String access_token, @Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> partMap);
}
