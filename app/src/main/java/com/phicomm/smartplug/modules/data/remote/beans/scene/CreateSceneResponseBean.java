package com.phicomm.smartplug.modules.data.remote.beans.scene;

import com.phicomm.smartplug.base.BaseResponseBean;

public class CreateSceneResponseBean extends BaseResponseBean {
    private String message;
    private long scenario_id;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getScenario_id() {
        return scenario_id;
    }

    public void setScenario_id(long scenario_id) {
        this.scenario_id = scenario_id;
    }
}
