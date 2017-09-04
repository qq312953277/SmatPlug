package com.phicomm.smartplug.modules.data.remote.beans.scene;

import com.phicomm.smartplug.base.BaseResponseBean;

import java.util.List;

public class GetSceneListResponseBean extends BaseResponseBean {
    private String message;
    private List<ScenarioListBean> scenario_list;

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

    public List<ScenarioListBean> getScenario_list() {
        return scenario_list;
    }

    public void setScenario_list(List<ScenarioListBean> scenario_list) {
        this.scenario_list = scenario_list;
    }

    public static class ScenarioListBean {
        private boolean auto;
        private boolean enable;
        private long lastExecuteTime;
        private long lastUpdateTime;
        private String scenario_description;
        private long scenario_id;
        private String scenario_name;
        private boolean scenario_status;
        private String trigger_alarm;
        private long uid;

        public boolean getAuto() {
            return auto;
        }

        public void setAuto(boolean auto) {
            this.auto = auto;
        }

        public boolean getEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public long getLastExecuteTime() {
            return lastExecuteTime;
        }

        public void setLastExecuteTime(long lastExecuteTime) {
            this.lastExecuteTime = lastExecuteTime;
        }

        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getScenario_description() {
            return scenario_description;
        }

        public void setScenario_description(String scenario_description) {
            this.scenario_description = scenario_description;
        }

        public long getScenario_id() {
            return scenario_id;
        }

        public void setScenario_id(long scenario_id) {
            this.scenario_id = scenario_id;
        }

        public String getScenario_name() {
            return scenario_name;
        }

        public void setScenario_name(String scenario_name) {
            this.scenario_name = scenario_name;
        }

        public boolean getScenario_status() {
            return scenario_status;
        }

        public void setScenario_status(boolean scenario_status) {
            this.scenario_status = scenario_status;
        }

        public String getTrigger_alarm() {
            return trigger_alarm;
        }

        public void setTrigger_alarm(String trigger_alarm) {
            this.trigger_alarm = trigger_alarm;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }
    }
}
