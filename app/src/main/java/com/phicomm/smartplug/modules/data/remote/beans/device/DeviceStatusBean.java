package com.phicomm.smartplug.modules.data.remote.beans.device;


import com.phicomm.smartplug.base.BaseResponseBean;

import java.util.ArrayList;

public class DeviceStatusBean extends BaseResponseBean {
    public int type;
    public DeviceDetail detail;
    public String message;
    public String reason;
    public String logID;
    public RespData respData;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class RespData {
        public String uuid;
        public int status;
        public ResultBean result;
        public String msg;
        public float totalElect;
        public ArrayList<PowerStaticsBean> dayList;
        public ArrayList<PowerStaticsBean> monthList;
    }

    public static class DeviceDetail {
        public boolean onlineState;
    }
}
