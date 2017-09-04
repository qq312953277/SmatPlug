package com.phicomm.smartplug.modules.scene.scenemain;

import android.content.Context;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneListResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TimeModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

public class ScenePresenter implements SceneContract.Presenter {
    private SceneContract.View mView;
    private Context mContext;
    private static String TAG = "ScenePresenter";

    public ScenePresenter(Context context, SceneContract.View view) {
        this.mView = view;
        mContext = context;
    }

    @Override
    public void getSceneLists() {
        // get token
        String access_token = DataRepository.getInstance().getAccessToken();

        // get product id
        String product_id = AppConstants.PRODUCT_ID;

        // upload data to server
        DataRepository.getInstance().getSceneList(mView.getRxLifeCycleObj(), new CustomSubscriber<GetSceneListResponseBean>() {
            @Override
            public void onCustomNext(GetSceneListResponseBean getSceneListResponseBean) {
                // cancel loading
                mView.cancelLoadingView();

                // update ui
                try {
                    int errorCode = Integer.parseInt(getSceneListResponseBean.getError());
                    if (errorCode == 0) {
                        List<SceneModel> scenes = parseSceneList(getSceneListResponseBean);
                        mView.updateSceneList(scenes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // update ui logic
                mView.analysisResponseBean(getSceneListResponseBean);
            }

            @Override
            public void onError(Throwable e) {
                DialogUtils.cancelLoadingDialog();
                mView.setRefreshComplete();
            }
        }, access_token, product_id);
    }

    private List<SceneModel> parseSceneList(GetSceneListResponseBean getSceneListResponseBean) {
        List<SceneModel> scenes = new ArrayList<>();

        if (getSceneListResponseBean != null
                && getSceneListResponseBean.getScenario_list() != null
                && getSceneListResponseBean.getScenario_list().size() > 0) {
            List<GetSceneListResponseBean.ScenarioListBean> beanScenes = getSceneListResponseBean.getScenario_list();
            for (GetSceneListResponseBean.ScenarioListBean beanScene : beanScenes) {

                // scene property
                SceneModel scene = new SceneModel();
                scene.setEnabled(beanScene.getEnable());
                scene.setSceneId(beanScene.getScenario_id());
                scene.setSceneName(beanScene.getScenario_name());
                scene.setLastExecuteTime(beanScene.getLastExecuteTime());
                scene.setLastUpdateTime(beanScene.getLastUpdateTime());
                scene.setSceneDescription(beanScene.getScenario_description());
                scene.setStatus(beanScene.getScenario_status());
                scene.setUid(beanScene.getUid());

                // time
                TimeModel time = new TimeModel();
                time.parseTimeStream(beanScene.getTrigger_alarm());

                // trigger
                TriggerModel trigger = new TriggerModel();
                trigger.setTriggerTime(time);
                trigger.setTriggerDescription(beanScene.getAuto() ? R.string.trigger_timer : R.string.trigger_manually);
                trigger.setImageId(beanScene.getAuto() ? R.drawable.scene_timer_trigger_icon : R.drawable.scene_manually_trigger_icon);
                trigger.setTriggerType(beanScene.getAuto() ? 1 : 0);
                scene.setTrigger(trigger);

                scenes.add(scene);
            }
        }

        return scenes;
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
}
