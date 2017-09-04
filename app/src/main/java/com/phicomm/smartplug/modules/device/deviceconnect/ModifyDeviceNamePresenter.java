package com.phicomm.smartplug.modules.device.deviceconnect;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

/**
 * Created by feilong.yang on 2017/7/11.
 */

public class ModifyDeviceNamePresenter implements ModifyDeviceNameContract.Presenter {
    private ModifyDeviceNameContract.View mBindView;

    public ModifyDeviceNamePresenter(ModifyDeviceNameContract.View bindView) {
        mBindView = bindView;
    }

    @Override
    public void modifyDeviceName(String access_token, String deviceID, String name) {
        DataRepository.getInstance().modifyDeviceName(mBindView.getRxLifeCycleObj(), new CustomSubscriber<DeviceResultBean>() {
            @Override
            public void onCustomNext(DeviceResultBean bean) {
                mBindView.analysisResponseBean(bean);
            }
        }, access_token, deviceID, name);
    }
}
