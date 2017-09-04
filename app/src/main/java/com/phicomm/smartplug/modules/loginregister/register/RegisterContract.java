package com.phicomm.smartplug.modules.loginregister.register;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public interface RegisterContract {

    interface View extends BaseView {
        //点击获取验证码按钮后，倒计时60s
        void startVerfyCodeTimerTask();

        //解析http response
        void analysisResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {

        //request phicomm authorization
        void doAuthorization(String client_id, String response_type, String scope, String client_secret);

        //register new account
        void doRegister(String authorizationcode, String phonenumber, String password, String registersource, String verificationcode);

        //check whether the account is registered
        void doCheckAccountRegistered(String authorizationcode, String phonenumber);

        //get sms verify code
        void doGetVerifyCode(String authorizationcode, String phonenumber, String verificationtype, String captcha, String captchaid);

        //get image code
        void doGetCaptchaImageCode(String authorizationcode);

        //request phicomm login
        void doPhoneLogin(String authorizationcode, String phonenumber, String password);
    }
}
