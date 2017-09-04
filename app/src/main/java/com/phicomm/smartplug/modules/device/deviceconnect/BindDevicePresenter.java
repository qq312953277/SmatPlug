package com.phicomm.smartplug.modules.device.deviceconnect;

import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.BindDeviceBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

/**
 * Created by feilong.yang on 2017/7/11.
 */

public class BindDevicePresenter implements BindDeviceContract.Presenter {
    private BindDeviceContract.View mBindView;

    public BindDevicePresenter(BindDeviceContract.View bindView) {
        mBindView = bindView;
    }

    @Override
    public void bindDevice(String access_token, String mac) {
        DataRepository.getInstance().bindDevice(mBindView.getRxLifeCycleObj(), new CustomSubscriber<BindDeviceBean>() {
            @Override
            public void onCustomNext(BindDeviceBean bean) {
                mBindView.analysisResponseBean(bean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                BaseResponseBean bean = new BaseResponseBean();
                bean.error = "-1";
                mBindView.analysisResponseBean(bean);

            }
        }, access_token, mac);
    }

    @Override
    public void unBindDevice(String access_token, String deviceId) {
        DataRepository.getInstance().unBindDevice(mBindView.getRxLifeCycleObj(), new CustomSubscriber<DeviceResultBean>() {
            @Override
            public void onCustomNext(DeviceResultBean bean) {
                mBindView.analysisResponseBean(bean);
            }
        }, access_token, deviceId);
    }
}
