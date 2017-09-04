package com.phicomm.smartplug.modules.scene.addscene.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneRequestBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;
import com.phicomm.smartplug.modules.scene.addscene.contract.AddSceneContract;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.utils.ConvertUtils;

public class AddScenePresenter implements AddSceneContract.Presenter {
    private AddSceneContract.View mView;
    private Context mContext;
    private static String TAG = "AddScenePresenter";

    public AddScenePresenter(AddSceneContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void createScene(SceneModel scene) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // convert scene to json
        CreateSceneRequestBean createSceneBean = ConvertUtils.convertSenceToCreateRequestBean(scene);
        Gson gson = new Gson();
        String createSceneJson = gson.toJson(createSceneBean);

        // upload data to server
        DataRepository.getInstance().createScene(mView.getRxLifeCycleObj(), new CustomSubscriber<CreateSceneResponseBean>() {
            @Override
            public void onCustomNext(CreateSceneResponseBean createSceneResponseBean) {
                mView.cancelLoadingView();
                mView.analysisResponseBean(createSceneResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
            }
        }, access_token, createSceneJson);
    }
}
