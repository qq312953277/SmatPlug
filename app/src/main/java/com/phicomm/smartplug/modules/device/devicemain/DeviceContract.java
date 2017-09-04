package com.phicomm.smartplug.modules.device.devicemain;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

public class DeviceContract {
    interface View extends BaseView {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);

        void analysisResponseBean(BaseResponseBean t, int position);

        void analysisError();

    }

    interface Presenter extends BasePresenter {
        //bind device
        void getDeviceLists(String access_token, String productID);

        void getDeviceConnState(String access_token, String deviceId, int position);
    }
}
