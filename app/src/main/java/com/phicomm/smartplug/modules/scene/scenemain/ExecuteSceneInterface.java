package com.phicomm.smartplug.modules.scene.scenemain;

import com.phicomm.smartplug.modules.scene.model.SceneModel;

public interface ExecuteSceneInterface {
    public void executeScene(boolean run, SceneModel scene, int triggerType);
}
