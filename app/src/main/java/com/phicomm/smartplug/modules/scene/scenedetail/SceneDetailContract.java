package com.phicomm.smartplug.modules.scene.scenedetail;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseView;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.DeleteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.scene.model.SceneModel;

public class SceneDetailContract {
    public interface View extends BaseView {
        void updateSceneDetailView(SceneModel scene, long currServerTime);

        void cancelLoadingView();

        void analysisDeleteSceneResponseBean(DeleteSceneResponseBean bean);

        void analysisExecuteSceneResponseBean(ExecuteSceneResponseBean bean);

        void analysisCancelSceneResponseBean(CancelSceneResponseBean bean);

        void analysisEditSceneResponseBean(EditSceneResponseBean bean);

        void showToast(int stringRes);
    }

    public interface Presenter extends BasePresenter {
        void getSceneDetail(long sceneId);

        void deleteScene(long sceneId);

        void executeScene(long sceneId);

        void cancelScene(long sceneId);

        void editScene(SceneModel scene);
    }
}
