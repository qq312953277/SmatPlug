package com.phicomm.smartplug.modules.scene.model;

import java.io.Serializable;

public class TaskModel implements Serializable {
    private long taskId;
    private String taskDescription;
    private String resultCode;
    private String currentstate;
    private long lastExecuteTime;
    private long lastUpdateTime;
    private long sceneId;
    private String taskMessage;

    private DeviceModel device;

    public TaskModel() {
        taskId = 0;
        taskDescription = "";
        device = new DeviceModel();
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public DeviceModel getDevice() {
        return device;
    }

    public void setDevice(DeviceModel device) {
        this.device = device;
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

    public long getSceneId() {
        return sceneId;
    }

    public void setSceneId(long sceneId) {
        this.sceneId = sceneId;
    }

    public String getTaskMessage() {
        return taskMessage;
    }

    public void setTaskMessage(String taskMessage) {
        this.taskMessage = taskMessage;
    }

    public int getTaskValue() {
        int taskValue = 0;
        boolean subOpened = false;
        int controlId = getDevice().getCurrentControl().getControlId();
        int operationValue = getDevice().getCurrentControl().getCurrentOperation().getOperation();

        for (int i = 3; i >= 0; i--) {
            if (i == controlId) {
                taskValue += operationValue * Math.pow(10, i);
                if (i > 0 && operationValue == OperationModel.OPEN) {
                    subOpened = true;
                }
            } else {
                if (i == 0) {
                    if (subOpened) {
                        taskValue += OperationModel.OPEN;
                    } else {
                        taskValue += OperationModel.NO_CHANGE;
                    }
                } else {
                    taskValue += OperationModel.NO_CHANGE * Math.pow(10, i);
                }
            }
        }

        return taskValue;
    }
}
