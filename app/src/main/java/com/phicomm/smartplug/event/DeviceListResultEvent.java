package com.phicomm.smartplug.event;

/**
 * Created by feilong.yang on 2017/7/20.
 */

public class DeviceListResultEvent {
    public String error;

    public DeviceListResultEvent(String error) {
        this.error = error;
    }
}
