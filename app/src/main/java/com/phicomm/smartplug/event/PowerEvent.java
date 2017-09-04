package com.phicomm.smartplug.event;

import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;

/**
 * Created by feilong.yang on 2017/7/24.
 */

public class PowerEvent {

    public DeviceStatusBean bean;

    public PowerEvent(DeviceStatusBean bean) {
        this.bean = bean;
    }
}
