package com.phicomm.smartplug.modules.data.remote.beans.device;

/**
 * Created by feilong.yang on 2017/7/19.
 */

public class ResultBean {
    public int status;
    public int I; //电流单位是毫安 mA
    public int V; //电压单位是伏特 V
    public int P; //功率单位是瓦特 W
    public String firmware_version;//固件版本号
}
