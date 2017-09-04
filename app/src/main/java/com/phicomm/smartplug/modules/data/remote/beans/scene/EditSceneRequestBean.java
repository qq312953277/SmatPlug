package com.phicomm.smartplug.modules.data.remote.beans.scene;

import java.util.List;

public class EditSceneRequestBean {
    private boolean auto;
    private boolean enable;
    private String scenario_description;
    private String scenario_name;
    private String trigger_alarm;
    private long scenario_id;
    private List<TaskBean> task_list;

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

    public String getScenario_description() {
        return scenario_description;
    }

    public void setScenario_description(String scenario_description) {
        this.scenario_description = scenario_description;
    }

    public String getScenario_name() {
        return scenario_name;
    }

    public void setScenario_name(String scenario_name) {
        this.scenario_name = scenario_name;
    }

    public String getTrigger_alarm() {
        return trigger_alarm;
    }

    public void setTrigger_alarm(String trigger_alarm) {
        this.trigger_alarm = trigger_alarm;
    }

    public long getScenario_id() {
        return scenario_id;
    }

    public void setScenario_id(long scenario_id) {
        this.scenario_id = scenario_id;
    }

    public List<TaskBean> getTask_list() {
        return task_list;
    }

    public void setTask_list(List<TaskBean> task_list) {
        this.task_list = task_list;
    }

    public static class TaskBean {
        private int control_id;
        private String dev_id;
        private String dev_name;
        private String task_description;
        private long task_id;
        private int task_value;
        private String time_delay_hour;
        private String time_delay_minute;

        public int getControl_id() {
            return control_id;
        }

        public void setControl_id(int control_id) {
            this.control_id = control_id;
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

        public int getTask_value() {
            return task_value;
        }

        public void setTask_value(int task_value) {
            this.task_value = task_value;
        }

        public String getTime_delay_hour() {
            return time_delay_hour;
        }

        public void setTime_delay_hour(String time_delay_hour) {
            this.time_delay_hour = time_delay_hour;
        }

        public String getTime_delay_minute() {
            return time_delay_minute;
        }

        public void setTime_delay_minute(String time_delay_minute) {
            this.time_delay_minute = time_delay_minute;
        }
    }
}
