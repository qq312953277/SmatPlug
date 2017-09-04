package com.phicomm.smartplug.modules.device.devicedetails;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by feilong.yang on 2017/7/13.
 */

public interface DeviceDetailContract {
    interface View extends BaseView {
        //解析http response
        void analysisInfoResponseBean(BaseResponseBean t);

        //解析http response
        void analysisControlResponseBean(BaseResponseBean t);
    }

    interface FirmwareView extends BaseView {
        //解析http response
        void analysisVersionResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //control device
        void getDeviceInfo(int type, int status, String deviceId, String access_token);

        void setDeviceControl(int type, int status, String deviceId, String access_token);

        //get device firmware version
        void getFirmwareVersion(String deviceId, String access_token);
    }
}
