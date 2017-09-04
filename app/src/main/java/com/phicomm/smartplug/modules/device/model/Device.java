package com.phicomm.smartplug.modules.device.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "Device".
 */
@Entity(indexes = {
        @Index(value = "deviceName, modifyTime DESC", unique = true)
})
public class Device {

    @Id
    private Long id;

    // 服务器返回的设备Id，目前是设备的mac地址
    private String deviceId;

    // 保留字段，后期可能用于设备访问控制
    private String accessKey;

    @NotNull
    private String deviceName;

    //总开关的名字
    public String switchMainName;
    //插口1的名字
    public String switch1Name;
    //插口2的名字
    public String switch2Name;
    //插口3的名字
    public String switch3Name;

    // 设备的缩略图地址
    private String deviceImgUrl;

    // 设备类型，服务器端的设备分类
    private String deviceType;

    // 设备状态
    private String status;

    // 服务器端的产品ID
    private Long productId;

    // 产品描述信息
    private String productDescription;

    // 产品缩略图
    private String productImgUrl;

    // 设备控制页的类型，分为H5控制和本地控制
    private String devicePageType;

    // 保留字段1
    private String reserved_1;

    // 保留字段2
    private String reserved_2;

    // 保留字段3
    private String reserved_3;

    // 本记录的修改时间
    private java.util.Date modifyTime;

    @Generated(hash = 1469582394)
    public Device() {
    }

    public Device(Long id) {
        this.id = id;
    }

    @Generated(hash = 705180650)
    public Device(Long id, String deviceId, String accessKey, @NotNull String deviceName,
                  String switchMainName, String switch1Name, String switch2Name, String switch3Name,
                  String deviceImgUrl, String deviceType, String status, Long productId,
                  String productDescription, String productImgUrl, String devicePageType,
                  String reserved_1, String reserved_2, String reserved_3,
                  java.util.Date modifyTime) {
        this.id = id;
        this.deviceId = deviceId;
        this.accessKey = accessKey;
        this.deviceName = deviceName;
        this.switchMainName = switchMainName;
        this.switch1Name = switch1Name;
        this.switch2Name = switch2Name;
        this.switch3Name = switch3Name;
        this.deviceImgUrl = deviceImgUrl;
        this.deviceType = deviceType;
        this.status = status;
        this.productId = productId;
        this.productDescription = productDescription;
        this.productImgUrl = productImgUrl;
        this.devicePageType = devicePageType;
        this.reserved_1 = reserved_1;
        this.reserved_2 = reserved_2;
        this.reserved_3 = reserved_3;
        this.modifyTime = modifyTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @NotNull
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setDeviceName(@NotNull String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImgUrl() {
        return deviceImgUrl;
    }

    public void setDeviceImgUrl(String deviceImgUrl) {
        this.deviceImgUrl = deviceImgUrl;
    }


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }

    public String getDevicePageType() {
        return devicePageType;
    }

    public void setDevicePageType(String devicePageType) {
        this.devicePageType = devicePageType;
    }

    public java.util.Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getReserved_1() {
        return reserved_1;
    }

    public void setReserved_1(String reserved_1) {
        this.reserved_1 = reserved_1;
    }

    public String getReserved_2() {
        return reserved_2;
    }

    public void setReserved_2(String reserved_2) {
        this.reserved_2 = reserved_2;
    }

    public String getReserved_3() {
        return reserved_3;
    }

    public void setReserved_3(String reserved_3) {
        this.reserved_3 = reserved_3;
    }

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
}
