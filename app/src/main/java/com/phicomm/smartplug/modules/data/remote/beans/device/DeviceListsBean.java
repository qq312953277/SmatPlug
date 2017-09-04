package com.phicomm.smartplug.modules.data.remote.beans.device;


import com.phicomm.smartplug.base.BaseResponseBean;

import java.util.List;

public class DeviceListsBean extends BaseResponseBean {
    public List<DeviceBean> devs;

    public static class DeviceBean {
        public DeviceAttributesBean attributes;
        public String deviceID;
        public String device_name;
        public String id;
        public boolean isOnline;
        public String onLineTitle = "";//在线，离线string

        public String phoneNumber;
        public String name;
        public String mac;

        public String getOnLineTitle() {
            return onLineTitle;
        }

        public void setOnLineTitle(String onLineTitle) {
            this.onLineTitle = onLineTitle;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public DeviceAttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(DeviceAttributesBean attributes) {
            this.attributes = attributes;
        }

        public String getDeviceID() {
            return deviceID;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
