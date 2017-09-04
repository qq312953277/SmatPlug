package com.phicomm.smartplug.modules.device.devicemodify;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

/**
 * Created by feilong.yang on 2017/7/11.
 */

public class ModifyDevicePresenter implements ModifyDeviceContract.Presenter {
    private ModifyDeviceContract.View mView;

    public ModifyDevicePresenter(ModifyDeviceContract.View view) {
        mView = view;
    }

    /**
     * 修改设备插口名称
     *
     * @param access_token
     * @param attributes
     * @param deviceId
     */
    @Override
    public void modifyDevice(String access_token, String attributes, String deviceId) {
        DataRepository.getInstance().modifyDevice(mView.getRxLifeCycleObj(), new CustomSubscriber<DeviceResultBean>() {
            @Override
            public void onCustomNext(DeviceResultBean bean) {
                mView.analysisModifyResponseBean(bean);
            }
        }, access_token, attributes, deviceId);
    }

}
