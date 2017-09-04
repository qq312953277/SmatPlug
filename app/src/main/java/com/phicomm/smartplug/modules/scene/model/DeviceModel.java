package com.phicomm.smartplug.modules.scene.model;

import java.io.Serializable;
import java.util.List;

public class DeviceModel implements Serializable {
    private String deviceName;
    private String deviceId;
    private int deviceType;
    private int imageId;
    private String productId;
    private ControlModel currentControl;
    private List<ControlModel> controls;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ControlModel getCurrentControl() {
        return currentControl;
    }

    public void setCurrentControl(ControlModel currentControl) {
        this.currentControl = currentControl;
    }

    public List<ControlModel> getControls() {
        return controls;
    }

    public void setControls(List<ControlModel> controls) {
        this.controls = controls;
    }
}
