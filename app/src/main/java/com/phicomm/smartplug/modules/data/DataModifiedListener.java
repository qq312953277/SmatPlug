package com.phicomm.smartplug.modules.data;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public interface DataModifiedListener {

    /**
     * Notify the latest remote wearable device.
     *
     * @param name  the Key name of Settings data
     * @param value the value of Settings data
     */
    void onSettingsDataModified(String name, String value);

    /**
     * Notify the latest remote wearable device.
     *
     * @param trainId the train Id of the train item added to Database
     */
    void onTrainListAdded(String trainId);

    /**
     * Notify the latest remote wearable device.
     *
     * @param planId the plain Did of Modified Plan item
     */
    void onPlanStateModified(String planId);

    /**
     * Notify RunList changed.
     */
    void onRunListModified();

    /**
     * 接口适配器模式(ˇˍˇ)～
     */
    abstract class DataModifiedListenerAdapter implements DataModifiedListener {
        public void onSettingsDataModified(String name, String value) {

        }

        public void onTrainListAdded(String trainId) {

        }

        public void onPlanStateModified(String planId) {

        }

        public void onRunListModified() {

        }
    }
}
