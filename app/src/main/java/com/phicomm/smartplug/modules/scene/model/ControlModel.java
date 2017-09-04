package com.phicomm.smartplug.modules.scene.model;

import java.io.Serializable;
import java.util.List;

public class ControlModel implements Serializable {
    private int controlId;
    private String controlName;
    private OperationModel currentOperation;
    private List<OperationModel> operations;

    private TimeModel delayTime;


    public int getControlId() {
        return controlId;
    }

    public void setControlId(int controlId) {
        this.controlId = controlId;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public List<OperationModel> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationModel> operations) {
        this.operations = operations;
    }

    public TimeModel getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(TimeModel delayTime) {
        this.delayTime = delayTime;
    }

    public OperationModel getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(OperationModel currentOperation) {
        this.currentOperation = currentOperation;
    }
}
