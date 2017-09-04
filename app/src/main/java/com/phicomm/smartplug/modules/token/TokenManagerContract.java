package com.phicomm.smartplug.modules.token;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;

/**
 * Created by yun.wang
 * Date :2017/6/28
 * Description: ***
 * Version: 1.0.0
 */

public interface TokenManagerContract {

    interface View {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //刷新token
        void refreshToken(String refreshToken, String authorizationCode);
    }
}
