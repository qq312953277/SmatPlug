package com.phicomm.smartplug.modules.scene.addscene.contract;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseView;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneResponseBean;
import com.phicomm.smartplug.modules.scene.model.SceneModel;

public class AddSceneContract {
    public interface View extends BaseView {
        void cancelLoadingView();

        void analysisResponseBean(CreateSceneResponseBean bean);
    }

    public interface Presenter extends BasePresenter {
        void createScene(SceneModel scene);
    }
}
