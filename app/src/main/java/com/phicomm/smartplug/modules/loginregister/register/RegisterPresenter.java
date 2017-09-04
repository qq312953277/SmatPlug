package com.phicomm.smartplug.modules.loginregister.register;


import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.RegisterResponseBean;
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

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mRegisterView;

    public RegisterPresenter(RegisterContract.View mRegisterView) {
        this.mRegisterView = mRegisterView;
    }

    @Override
    public void doAuthorization(String client_id, String response_type, String scope, String client_secret) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", client_id);
        parameters.put("response_type", response_type);
        parameters.put("scope", scope);
        parameters.put("client_secret", client_secret);
        DataRepository.getInstance().getAuthorization(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<AuthorizationResponseBean>() {
                    @Override
                    public void onCustomNext(AuthorizationResponseBean authorizationResponseBean) {
                        mRegisterView.analysisResponseBean(authorizationResponseBean);
                    }

                }, parameters);
    }

    @Override
    public void doCheckAccountRegistered(String authorizationcode, String phonenumber) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        parameters.put("phonenumber", phonenumber);
        DataRepository.getInstance().checkPhoneNumber(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<CheckphonenumberResponseBean>() {

                    @Override
                    public void onCustomNext(CheckphonenumberResponseBean checkphonenumberResponseBean) {
                        mRegisterView.analysisResponseBean(checkphonenumberResponseBean);
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
        DataRepository.getInstance().verifyCode(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<VerifycodeResponseBean>() {

                    @Override
                    public void onCustomNext(VerifycodeResponseBean verifycodeResponseBean) {
                        mRegisterView.analysisResponseBean(verifycodeResponseBean);
                    }

                }, parameters);
    }

    @Override
    public void doGetCaptchaImageCode(String authorizationcode) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        DataRepository.getInstance().captcha(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<CaptchaResponseBean>() {
                    @Override
                    public void onCustomNext(CaptchaResponseBean captchaResponseBean) {
                        mRegisterView.analysisResponseBean(captchaResponseBean);
                    }

                }, parameters);
    }

    @Override
    public void doRegister(String authorizationcode, String phonenumber, String password, String registersource, String verificationcode) {
        RequestBody formBody = new FormBody.Builder()
                .add("authorizationcode", authorizationcode)
                .add("phonenumber", phonenumber)
                .add("password", password)
                .add("registersource", registersource)
                .add("verificationcode", verificationcode)
                .build();
        DataRepository.getInstance().register(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<RegisterResponseBean>() {
                    @Override
                    public void onCustomNext(RegisterResponseBean registerResponseBean) {
                        mRegisterView.analysisResponseBean(registerResponseBean);
                    }
                }, formBody);
    }

    @Override
    public void doPhoneLogin(String authorizationcode, String phonenumber, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("authorizationcode", authorizationcode)
                .add("phonenumber", phonenumber)
                .add("password", password)
                .build();
        DataRepository.getInstance().login(mRegisterView.getRxLifeCycleObj(),
                new CustomSubscriber<LoginResponseBean>() {
                    @Override
                    public void onCustomNext(LoginResponseBean loginResponseBean) {
                        mRegisterView.analysisResponseBean(loginResponseBean);
                    }
                }, formBody);
    }
}
