package com.phicomm.smartplug.modules.data.remote.beans.device;

import com.phicomm.smartplug.base.BaseResponseBean;

/**
 * Created by feilong.yang on 2017/7/11.
 */

public class BindDeviceBean extends BaseResponseBean {
    public DeviceBean result;

    public static class DeviceBean {
        public String name;
        public DeviceAttributesBean attributes;
        public String deviceID;
        public String mac;
    }

}
