package com.phicomm.smartplug.event;


import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;

/**
 * Created by feilong.yang on 2017/7/19.
 */

public class ModifyDeviceEvent {

    public DeviceResultBean mBean;

    public ModifyDeviceEvent() {

    }

    public ModifyDeviceEvent(DeviceResultBean bean) {
        this.mBean = bean;
    }
}
