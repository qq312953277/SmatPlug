package com.phicomm.smartplug.modules.loginregister.registerloginmain;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public interface RegisterLoginContract {
    interface View extends BaseView {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //request phicomm authorization
        void doAuthorization(String client_id, String response_type, String scope, String client_secret);

        //request phicomm login
        void doPhoneLogin(String authorizationcode, String phonenumber, String password);
    }
}
