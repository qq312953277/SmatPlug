package com.phicomm.smartplug.modules.personal.modifypassword;


import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
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

public class ModifyPasswordPresenter implements ModifyPasswordContract.Presenter {

    private ModifyPasswordContract.View myView;

    public ModifyPasswordPresenter(ModifyPasswordContract.View myView) {
        this.myView = myView;
    }

    @Override
    public void doAuthorization(String client_id, String response_type, String scope, String client_secret) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", client_id);
        parameters.put("response_type", response_type);
        parameters.put("scope", scope);
        parameters.put("client_secret", client_secret);
        DataRepository.getInstance().getAuthorization(myView.getRxLifeCycleObj(), new CustomSubscriber<AuthorizationResponseBean>() {
            @Override
            public void onCustomNext(AuthorizationResponseBean authorizationResponseBean) {
                myView.analysisResponseBean(authorizationResponseBean);
            }

        }, parameters);
    }

    @Override
    public void doModifyPassword(String oldpassword, String newpassword) {

        String access_token = DataRepository.getInstance().getAccessToken();

        RequestBody formBody = new FormBody.Builder()
                .add("oldpassword", oldpassword)
                .add("newpassword", newpassword)
                .build();
        DataRepository.getInstance().modifyPassword(myView.getRxLifeCycleObj(),
                new CustomSubscriber<ModifypasswordResponseBean>() {
                    @Override
                    public void onCustomNext(ModifypasswordResponseBean modifyResponseBean) {
                        myView.analysisResponseBean(modifyResponseBean);
                    }
                }, access_token, formBody);
    }
}
