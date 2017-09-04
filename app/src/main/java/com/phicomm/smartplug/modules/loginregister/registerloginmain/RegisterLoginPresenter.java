package com.phicomm.smartplug.modules.loginregister.registerloginmain;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
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

public class RegisterLoginPresenter implements RegisterLoginContract.Presenter {

    private RegisterLoginContract.View mView;

    public RegisterLoginPresenter(RegisterLoginContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void doPhoneLogin(String authorizationcode, String phonenumber, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("authorizationcode", authorizationcode)
                .add("phonenumber", phonenumber)
                .add("password", password)
                .build();
        DataRepository.getInstance().login(mView.getRxLifeCycleObj(),
                new CustomSubscriber<LoginResponseBean>() {
                    @Override
                    public void onCustomNext(LoginResponseBean loginResponseBean) {
                        mView.analysisResponseBean(loginResponseBean);
                    }
                }, formBody);
    }

    @Override
    public void doAuthorization(String client_id, String response_type, String scope, String client_secret) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", client_id);
        parameters.put("response_type", response_type);
        parameters.put("scope", scope);
        parameters.put("client_secret", client_secret);
        DataRepository.getInstance().getAuthorization(mView.getRxLifeCycleObj(),
                new CustomSubscriber<AuthorizationResponseBean>() {
                    @Override
                    public void onCustomNext(AuthorizationResponseBean authorizationResponseBean) {
                        mView.analysisResponseBean(authorizationResponseBean);
                    }

                }, parameters);
    }
}
