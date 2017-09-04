package com.phicomm.smartplug.modules.device.devicemain;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

public class DevicePresenter implements DeviceContract.Presenter {
    private DeviceContract.View mDeviceView;

    public DevicePresenter(DeviceContract.View view) {
        this.mDeviceView = view;
    }

    @Override
    public void getDeviceLists(String access_token, String productID) {

        DataRepository.getInstance().getBindDevs(mDeviceView.getRxLifeCycleObj(), new CustomSubscriber<DeviceListsBean>() {
            @Override
            public void onCustomNext(DeviceListsBean deviceListBean) {
                mDeviceView.analysisResponseBean(deviceListBean);
            }
        }, access_token, productID);
    }

    @Override
    public void getDeviceConnState(String access_token, String deviceId, final int poisition) {
        DataRepository.getInstance().getDeviceStatus(mDeviceView.getRxLifeCycleObj(), new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mDeviceView.analysisResponseBean(deviceStatusBean, poisition);
            }
        }, access_token, deviceId);
    }
}
