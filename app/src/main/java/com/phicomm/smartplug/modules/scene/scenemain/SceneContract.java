package com.phicomm.smartplug.modules.scene.scenemain;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseView;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneListResponseBean;
import com.phicomm.smartplug.modules.scene.model.SceneModel;

import java.util.List;

public class SceneContract {
    public interface View extends BaseView {
        void analysisResponseBean(GetSceneListResponseBean bean);

        void updateSceneList(List<SceneModel> sceneList);

        void setRefreshComplete();

        void cancelLoadingView();

        void analysisExecuteSceneResponseBean(ExecuteSceneResponseBean bean);

        void analysisCancelSceneResponseBean(CancelSceneResponseBean bean);
    }

    public interface Presenter extends BasePresenter {
        void getSceneLists();

        void executeScene(long sceneId);

        void cancelScene(long sceneId);
    }
}
