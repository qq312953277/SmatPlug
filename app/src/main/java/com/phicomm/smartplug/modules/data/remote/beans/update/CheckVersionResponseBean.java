package com.phicomm.smartplug.modules.data.remote.beans.update;

import com.phicomm.smartplug.base.BaseResponseBean;

public class CheckVersionResponseBean extends BaseResponseBean {
    private int ret;
    private String id;
    private int verType;
    private String verCode;
    private String verName;
    private String verTime;
    private String verInfos;
    private String verDown;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVerType() {
        return verType;
    }

    public void setVerType(int verType) {
        this.verType = verType;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getVerTime() {
        return verTime;
    }

    public void setVerTime(String verTime) {
        this.verTime = verTime;
    }

    public String getVerInfos() {
        return verInfos;
    }

    public void setVerInfos(String verInfos) {
        this.verInfos = verInfos;
    }

    public String getVerDown() {
        return verDown;
    }

    public void setVerDown(String verDown) {
        this.verDown = verDown;
    }
}
