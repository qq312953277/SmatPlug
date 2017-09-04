package com.phicomm.smartplug.modules.scene.model;

import com.phicomm.smartplug.R;

import java.io.Serializable;

public class TriggerModel implements Serializable {
    public static final int TRIGGER_MANUALLY = 0;
    public static final int TRIGGER_TIMER = 1;

    private int triggerType;
    private int imageId;
    private TimeModel triggerTime;
    private int triggerDescription;

    public TriggerModel() {
        triggerType = TRIGGER_MANUALLY;
        imageId = 0;
        triggerTime = new TimeModel();
        triggerDescription = 0;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getTriggerName() {
        switch (triggerType) {
            case TRIGGER_MANUALLY:
                return R.string.trigger_manually;
            case TRIGGER_TIMER:
                return R.string.trigger_timer;
            default:
                return R.string.trigger_manually;
        }
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public TimeModel getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(TimeModel triggerTime) {
        this.triggerTime = triggerTime;
    }

    public int getTriggerDescription() {
        return triggerDescription;
    }

    public void setTriggerDescription(int triggerDescription) {
        this.triggerDescription = triggerDescription;
    }
}

