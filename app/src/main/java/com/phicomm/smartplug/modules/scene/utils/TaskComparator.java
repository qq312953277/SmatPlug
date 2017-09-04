package com.phicomm.smartplug.modules.scene.utils;

import com.phicomm.smartplug.modules.scene.model.TaskModel;

import java.io.Serializable;
import java.util.Comparator;

public class TaskComparator implements Serializable, Comparator<TaskModel> {
    @Override
    public int compare(TaskModel o1, TaskModel o2) {
        try {
            if (o1.getDevice().getCurrentControl().getDelayTime().getHour() > o2.getDevice().getCurrentControl().getDelayTime().getHour()) {
                return 1;
            } else if (o1.getDevice().getCurrentControl().getDelayTime().getHour() == o2.getDevice().getCurrentControl().getDelayTime().getHour()) {
                if (o1.getDevice().getCurrentControl().getDelayTime().getMinute() >= o2.getDevice().getCurrentControl().getDelayTime().getMinute()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
