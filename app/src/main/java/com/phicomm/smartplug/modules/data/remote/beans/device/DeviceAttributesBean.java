package com.phicomm.smartplug.modules.data.remote.beans.device;

import java.io.Serializable;

/**
 * Created by feilong.yang on 2017/7/18.
 */

public class DeviceAttributesBean implements Serializable {

    private static final long serialVersionUID = -4070356572795055664L;
    //总开关的名字
    private String switchMainName;
    //插口1的名字
    private String switch1Name;
    //插口2的名字
    private String switch2Name;
    //插口3的名字
    private String switch3Name;

    public String exception;

    private String mac;

    public String getSwitchMainName() {
        return switchMainName;
    }

    public void setSwitchMainName(String switchMainName) {
        this.switchMainName = switchMainName;
    }

    public String getSwitch1Name() {
        return switch1Name;
    }

    public void setSwitch1Name(String switch1Name) {
        this.switch1Name = switch1Name;
    }

    public String getSwitch2Name() {
        return switch2Name;
    }

    public void setSwitch2Name(String switch2Name) {
        this.switch2Name = switch2Name;
    }

    public String getSwitch3Name() {
        return switch3Name;
    }

    public void setSwitch3Name(String switch3Name) {
        this.switch3Name = switch3Name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
