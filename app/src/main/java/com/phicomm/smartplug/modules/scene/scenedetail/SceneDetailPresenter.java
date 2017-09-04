package com.phicomm.smartplug.modules.scene.scenedetail;

import android.content.Context;

import com.google.gson.Gson;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.DeleteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneRequestBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneDetailResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.utils.ConvertUtils;

public class SceneDetailPresenter implements SceneDetailContract.Presenter {
    private SceneDetailContract.View mView;
    private static String TAG = "ScenePresenter";

    public SceneDetailPresenter(Context context, SceneDetailContract.View view) {
        this.mView = view;
    }

    @Override
    public void getSceneDetail(long sceneId) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        DataRepository.getInstance().getSceneDetail(mView.getRxLifeCycleObj(), new CustomSubscriber<GetSceneDetailResponseBean>() {
            @Override
            public void onCustomNext(GetSceneDetailResponseBean getSceneDetailResponseBean) {
                // cancel loading
                mView.cancelLoadingView();

                // update ui
                try {
                    int errorCode = Integer.parseInt(getSceneDetailResponseBean.getError());
                    if (errorCode == 0) {
                        SceneModel scene = ConvertUtils.convertSceneDetailBeanToSceneModel(getSceneDetailResponseBean);
                        mView.updateSceneDetailView(scene, getSceneDetailResponseBean.getServerTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
                mView.showToast(R.string.common_error);
            }
        }, access_token, sceneId);
    }

    @Override
    public void deleteScene(long sceneId) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // perform delete
        DataRepository.getInstance().deleteScene(mView.getRxLifeCycleObj(), new CustomSubscriber<DeleteSceneResponseBean>() {
            @Override
            public void onCustomNext(DeleteSceneResponseBean deleteSceneResponseBean) {
                mView.cancelLoadingView();
                mView.analysisDeleteSceneResponseBean(deleteSceneResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
            }
        }, access_token, sceneId);
    }

    @Override
    public void executeScene(long sceneId) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // perform execute
        DataRepository.getInstance().executeScene(mView.getRxLifeCycleObj(), new CustomSubscriber<ExecuteSceneResponseBean>() {
            @Override
            public void onCustomNext(ExecuteSceneResponseBean executeSceneResponseBean) {
                mView.cancelLoadingView();
                mView.analysisExecuteSceneResponseBean(executeSceneResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
            }
        }, access_token, sceneId);
    }

    @Override
    public void cancelScene(long sceneId) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // perform execute
        DataRepository.getInstance().cancelScene(mView.getRxLifeCycleObj(), new CustomSubscriber<CancelSceneResponseBean>() {
            @Override
            public void onCustomNext(CancelSceneResponseBean cancelSceneResponseBean) {
                mView.cancelLoadingView();
                mView.analysisCancelSceneResponseBean(cancelSceneResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
            }
        }, access_token, sceneId);
    }

    @Override
    public void editScene(SceneModel scene) {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // convert scene to json
        EditSceneRequestBean editSceneBean = ConvertUtils.convertSenceToEditRequestBean(scene);
        Gson gson = new Gson();
        String editSceneJson = gson.toJson(editSceneBean);

        // upload data to server
        DataRepository.getInstance().editScene(mView.getRxLifeCycleObj(), new CustomSubscriber<EditSceneResponseBean>() {
            @Override
            public void onCustomNext(EditSceneResponseBean editSceneResponseBean) {
                mView.cancelLoadingView();
                mView.analysisEditSceneResponseBean(editSceneResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.cancelLoadingView();
            }
        }, access_token, editSceneJson);
    }
}
