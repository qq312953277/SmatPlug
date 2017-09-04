package com.phicomm.smartplug.modules.data.remote.beans.scene;

import com.phicomm.smartplug.base.BaseResponseBean;

import java.util.List;

public class GetSceneDetailResponseBean extends BaseResponseBean {
    private String message;
    private long serverTime;
    private ScenarioBean scenario;

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

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public ScenarioBean getScenario() {
        return scenario;
    }

    public void setScenario(ScenarioBean scenario) {
        this.scenario = scenario;
    }

    public static class ScenarioBean {
        private boolean auto;
        private boolean enable;
        private boolean is_running;
        private long lastExecuteTime;
        private long lastUpdateTime;
        private String scenario_description;
        private long scenario_id;
        private String scenario_name;
        private boolean scenario_status;
        private String trigger_alarm;
        private String uid;
        private List<TaskListBean> task_list;

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

        public boolean is_running() {
            return is_running;
        }

        public void setIs_running(boolean is_running) {
            this.is_running = is_running;
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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public List<TaskListBean> getTask_list() {
            return task_list;
        }

        public void setTask_list(List<TaskListBean> task_list) {
            this.task_list = task_list;
        }

        public static class TaskListBean {
            private int control_id;
            private String control_name;
            private String dev_id;
            private String dev_name;
            private long lastExecuteTime;
            private long lastUpdateTime;
            private String resultCode;
            private long scenario_id;
            private String task_description;
            private long task_id;
            private String task_message;
            private int task_value;
            private String currentstate;
            private int time_delay_hour;
            private int time_delay_minute;

            public int getControl_id() {
                return control_id;
            }

            public void setControl_id(int control_id) {
                this.control_id = control_id;
            }

            public String getControl_name() {
                return control_name;
            }

            public void setControl_name(String control_name) {
                this.control_name = control_name;
            }

            public String getDev_id() {
                return dev_id;
            }

            public void setDev_id(String dev_id) {
                this.dev_id = dev_id;
            }

            public String getDev_name() {
                return dev_name;
            }

            public void setDev_name(String dev_name) {
                this.dev_name = dev_name;
            }

            public String getResultCode() {
                return resultCode;
            }

            public void setResultCode(String resultCode) {
                this.resultCode = resultCode;
            }

            public String getCurrentstate() {
                return currentstate;
            }

            public void setCurrentstate(String currentstate) {
                this.currentstate = currentstate;
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

            public long getScenario_id() {
                return scenario_id;
            }

            public void setScenario_id(long scenario_id) {
                this.scenario_id = scenario_id;
            }

            public String getTask_description() {
                return task_description;
            }

            public void setTask_description(String task_description) {
                this.task_description = task_description;
            }

            public long getTask_id() {
                return task_id;
            }

            public void setTask_id(long task_id) {
                this.task_id = task_id;
            }

            public String getTask_message() {
                return task_message;
            }

            public void setTask_message(String task_message) {
                this.task_message = task_message;
            }

            public int getTask_value() {
                return task_value;
            }

            public void setTask_value(int task_value) {
                this.task_value = task_value;
            }

            public int getTime_delay_hour() {
                return time_delay_hour;
            }

            public void setTime_delay_hour(int time_delay_hour) {
                this.time_delay_hour = time_delay_hour;
            }

            public int getTime_delay_minute() {
                return time_delay_minute;
            }

            public void setTime_delay_minute(int time_delay_minute) {
                this.time_delay_minute = time_delay_minute;
            }
        }
    }
}
