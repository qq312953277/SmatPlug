package com.phicomm.smartplug.modules.scene.model;

import com.phicomm.smartplug.R;

import java.io.Serializable;

public class OperationModel implements Serializable {
    public static final int CLOSE = 0;
    public static final int OPEN = 1;
    public static final int NO_CHANGE = 2;

    private int operation;


    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getOperationName() {
        switch (operation) {
            case OPEN:
                return R.string.operation_open;
            case CLOSE:
                return R.string.operation_close;
            default:
                return R.string.operation_no_change;
        }
    }
}
