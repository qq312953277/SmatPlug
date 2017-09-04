package com.phicomm.smartplug.modules.token;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.TokenUpdateBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yun.wang
 * Date :2017/6/28
 * Description: ***
 * Version: 1.0.0
 */

public class TokenManagerPresenter implements TokenManagerContract.Presenter {

    private TokenManagerContract.View myTokenView;

    public TokenManagerPresenter(TokenManagerContract.View myView) {
        myTokenView = myView;

    }

    @Override
    public void refreshToken(String refreshToken, String authorizationcode) {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("authorizationcode", authorizationcode);
        parameters.put("grant_type", "refresh_token");
        DataRepository.getInstance().refreshToken(myTokenView,
                new CustomSubscriber<TokenUpdateBean>() {

                    @Override
                    public void onCustomNext(TokenUpdateBean tokenUpdateBean) {
                        myTokenView.analysisResponseBean(tokenUpdateBean);
                    }

                }, refreshToken, parameters);
    }
}
