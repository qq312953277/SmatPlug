package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.account.AccountDetailBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypropertyResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.ForgetpasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.RegisterResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.TokenUpdateBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.VerifycodeResponseBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public interface HttpCloudInterface {

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("authorization")
    Observable<AuthorizationResponseBean> authorization(@QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("verificationCode")
    Observable<VerifycodeResponseBean> verifyCode(@QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("verificationMsg")
    Observable<VerifycodeResponseBean> verifyCodeCaptcha(@QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("captcha")
    Observable<CaptchaResponseBean> captcha(@QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("checkPhonenumber")
    Observable<CheckphonenumberResponseBean> checkPhonenumber(@QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("token")
    Observable<TokenUpdateBean> refreshToken(@Header("Authorization") String access_token, @QueryMap Map<String, String> map);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("login")
    Observable<LoginResponseBean> login(@Body RequestBody body);  //RequestBody待提交的参数 POST请求必须

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("account")
    Observable<RegisterResponseBean> register(@Body RequestBody body);

    @POST("forgetpassword")
    Observable<ForgetpasswordResponseBean> forgetPassword(@Body RequestBody body);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("accountDetail")
    Observable<AccountDetailBean> accountDetail(@Header("Authorization") String access_token);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("password")
    Observable<ModifypasswordResponseBean> modifyPassword(@Header("Authorization") String access_token, @Body RequestBody body);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("property")
    Observable<ModifypropertyResponseBean> modifyProperty(@Header("Authorization") String access_token, @Body RequestBody body);
}
