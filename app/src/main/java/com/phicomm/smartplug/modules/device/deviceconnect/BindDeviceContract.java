package com.phicomm.smartplug.modules.device.deviceconnect;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by feilong.yang on 2017/7/11.
 */

public interface BindDeviceContract {
    interface View extends BaseView {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //bind device
        void bindDevice(String access_token, String mac);

        //unbind device
        void unBindDevice(String access_token, String deviceId);
    }
}
