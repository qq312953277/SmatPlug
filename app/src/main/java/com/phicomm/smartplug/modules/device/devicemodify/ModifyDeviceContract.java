package com.phicomm.smartplug.modules.device.devicemodify;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by feilong.yang on 2017/7/13.
 */

public interface ModifyDeviceContract {
    interface View extends BaseView {
        //解析http response
        void analysisModifyResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //motify device socket name
        void modifyDevice(String access_token, String attributes, String deviceId);
    }
}
