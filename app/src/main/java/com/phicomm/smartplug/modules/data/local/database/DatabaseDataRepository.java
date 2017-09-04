package com.phicomm.smartplug.modules.data.local.database;

import android.content.Context;

import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceAttributesBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.device.model.Device;
import com.phicomm.smartplug.modules.device.model.DeviceDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class DatabaseDataRepository {

    private DeviceDao mDeviceDao;

    public DatabaseDataRepository(Context context) {
        if (null == mDeviceDao) {
            mDeviceDao = BaseApplication.getApplication().getDaoSession().getDeviceDao();
        }
    }

    //test code
    public void resetLocalDateBase() {

    }

    public String getSettingValue(String name, String defaultValue) {
        return "test";
    }

    public boolean setSettingsValue(String name, String defaultValue) {
        return true;
    }

    private void insert(Device device) {
        mDeviceDao.insert(device);
    }


    /**
     * 增
     *
     * @param deviceBean
     */
    public void insert(DeviceListsBean.DeviceBean deviceBean) {
        if (null == deviceBean)
            return;
        Device device = mDeviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceBean.getDeviceID())).build().unique();
        if (null != device) {
            updateDevice(deviceBean);
            return;
        }
        Device newDevice = new Device();
        String status = deviceBean.isOnline == true ? "true" : "false";
        newDevice.setId(null);
        newDevice.setDeviceId(deviceBean.getDeviceID());
        newDevice.setStatus(status);
        newDevice.setDeviceName(deviceBean.getName());
        DeviceAttributesBean attributes = deviceBean.getAttributes();
        newDevice.setSwitch1Name(attributes.getSwitch1Name());
        newDevice.setSwitch2Name(attributes.getSwitch2Name());
        newDevice.setSwitch3Name(attributes.getSwitch3Name());
        newDevice.setSwitchMainName(attributes.getSwitchMainName());
        insert(newDevice);
    }

    /**
     * 更新设备
     *
     * @param deviceBean
     */
    public void updateDevice(DeviceListsBean.DeviceBean deviceBean) {
        if (null == deviceBean)
            return;
        Device device = mDeviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceBean.getDeviceID())).build().unique();
        if (device != null) {
            String status = deviceBean.isOnline == true ? "true" : "false";
            device.setStatus(status);
            device.setDeviceName(deviceBean.getName());
            DeviceAttributesBean attributes = deviceBean.getAttributes();
            device.setSwitch1Name(attributes.getSwitch1Name());
            device.setSwitch2Name(attributes.getSwitch2Name());
            device.setSwitch3Name(attributes.getSwitch3Name());
            device.setSwitchMainName(attributes.getSwitchMainName());
            mDeviceDao.update(device);
        }
    }

    /**
     * 删
     *
     * @param deviceId
     */
    public void delete(String deviceId) {
        Device device = mDeviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceId)).build().unique();
        if (device != null) {
            mDeviceDao.delete(device);
        }
    }

    /**
     * 清空device
     */
    public void deleteAll() {
        mDeviceDao.deleteAll();
    }

    /**
     * 修改设备名称
     *
     * @param deviceId
     * @param deviceName
     */
    public void update(String deviceId, String deviceName) {
        Device device = mDeviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceId)).build().unique();
        if (device != null) {
            device.setDeviceName(deviceName);
            mDeviceDao.update(device);
        }
    }

    /**
     * 修改设备插口名称
     *
     * @param bean
     */
    public void updateDeviceSwitchName(DeviceResultBean bean) {
        if (null == bean)
            return;
        String deviceId = bean.result.deviceID;
        Device device = mDeviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceId)).build().unique();
        if (device != null) {
            DeviceAttributesBean attributes = bean.result.attributes;
            device.setSwitchMainName(attributes.getSwitchMainName());
            device.setSwitch1Name(attributes.getSwitch1Name());
            device.setSwitch2Name(attributes.getSwitch2Name());
            device.setSwitch3Name(attributes.getSwitch3Name());
            mDeviceDao.update(device);
        }
    }


    /**
     * 查
     *
     * @return
     */
    public ArrayList<DeviceListsBean.DeviceBean> query() {
        List<Device> listDevice = mDeviceDao.loadAll();
        ArrayList<DeviceListsBean.DeviceBean> devs = null;
        if (null != listDevice && listDevice.size() > 0) {
            devs = new ArrayList<DeviceListsBean.DeviceBean>();
            for (Device device : listDevice) {
                DeviceListsBean.DeviceBean bean = new DeviceListsBean.DeviceBean();
                bean.setOnline(device.getStatus().equals("true") ? true : false);
                bean.setDeviceID(device.getDeviceId());
                bean.setName(device.getDeviceName());
                DeviceAttributesBean attributes = new DeviceAttributesBean();
                attributes.setSwitch1Name(device.getSwitch1Name());
                attributes.setSwitch2Name(device.getSwitch2Name());
                attributes.setSwitch3Name(device.getSwitch3Name());
                attributes.setSwitchMainName(device.getSwitchMainName());
                bean.setAttributes(attributes);
                devs.add(bean);
            }
        }
        return devs;
    }

}
