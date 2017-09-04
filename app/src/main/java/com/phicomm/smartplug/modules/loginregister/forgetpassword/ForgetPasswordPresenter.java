package com.phicomm.smartplug.modules.loginregister.forgetpassword;


import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.ForgetpasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.VerifycodeResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class ForgetPasswordPresenter implements ForgetPasswordContract.Presenter {

    private ForgetPasswordContract.View mResetPasswordView;

    public ForgetPasswordPresenter(ForgetPasswordContract.View mRegisterView) {
        this.mResetPasswordView = mRegisterView;
    }

    @Override
    public void doAuthorization(String client_id, String response_type, String scope, String client_secret) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", client_id);
        parameters.put("response_type", response_type);
        parameters.put("scope", scope);
        parameters.put("client_secret", client_secret);
        DataRepository.getInstance().getAuthorization(mResetPasswordView.getRxLifeCycleObj(), new CustomSubscriber<AuthorizationResponseBean>() {
            @Override
            public void onCustomNext(AuthorizationResponseBean authorizationResponseBean) {
                mResetPasswordView.analysisResponseBean(authorizationResponseBean);
            }

        }, parameters);
    }

    @Override
    public void doResetPassword(String authorizationcode, String phonenumber, String newpassword, String verificationcode) {
        RequestBody formBody = new FormBody.Builder()
                .add("authorizationcode", authorizationcode) //待提交的参数
                .add("phonenumber", phonenumber)
                .add("newpassword", newpassword)
                .add("verificationcode", verificationcode)
                .build();
        DataRepository.getInstance().forgetPassword(mResetPasswordView.getRxLifeCycleObj(),
                new CustomSubscriber<ForgetpasswordResponseBean>() {
                    @Override
                    public void onCustomNext(ForgetpasswordResponseBean forgetpasswordResponseBean) {
                        mResetPasswordView.analysisResponseBean(forgetpasswordResponseBean);
                    }
                }, formBody);
    }

    @Override
    public void doCheckAccountRegistered(String authorizationcode, String phonenumber) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        parameters.put("phonenumber", phonenumber);
        DataRepository.getInstance().checkPhoneNumber(mResetPasswordView.getRxLifeCycleObj(),
                new CustomSubscriber<CheckphonenumberResponseBean>() {

                    @Override
                    public void onCustomNext(CheckphonenumberResponseBean checkphonenumberResponseBean) {
                        mResetPasswordView.analysisResponseBean(checkphonenumberResponseBean);
                    }

                }, parameters);
    }

    @Override
    public void doGetVerifyCode(String authorizationcode, String phonenumber, String verificationtype, String captcha, String captchaid) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        parameters.put("phonenumber", phonenumber);
        parameters.put("verificationtype", verificationtype);
        parameters.put("captcha", captcha);
        parameters.put("captchaid", captchaid);
        DataRepository.getInstance().verifyCode(mResetPasswordView.getRxLifeCycleObj(),
                new CustomSubscriber<VerifycodeResponseBean>() {

                    @Override
                    public void onCustomNext(VerifycodeResponseBean verifycodeResponseBean) {
                        mResetPasswordView.analysisResponseBean(verifycodeResponseBean);
                    }

                }, parameters);
    }

    @Override
    public void doGetCaptchaImageCode(String authorizationcode) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        DataRepository.getInstance().captcha(mResetPasswordView.getRxLifeCycleObj(),
                new CustomSubscriber<CaptchaResponseBean>() {
                    @Override
                    public void onCustomNext(CaptchaResponseBean captchaResponseBean) {
                        mResetPasswordView.analysisResponseBean(captchaResponseBean);
                    }

                }, parameters);
    }
}
