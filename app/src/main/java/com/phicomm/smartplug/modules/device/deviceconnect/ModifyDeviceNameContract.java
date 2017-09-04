package com.phicomm.smartplug.modules.device.deviceconnect;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by feilong.yang on 2017/7/12.
 */

public interface ModifyDeviceNameContract {
    interface View extends BaseView {
        //解析http response
        void analysisResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //modify device name
        void modifyDeviceName(String access_token, String deviceID, String name);
    }
}
