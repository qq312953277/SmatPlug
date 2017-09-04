package com.phicomm.smartplug.modules.device.devicedetails;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.base.BaseView;

/**
 * Created by feilong.yang on 2017/7/13.
 */

public interface PowerStaticsContract {
    interface View extends BaseView {
        //解析月电量http response
        void analysisDayElectResponseBean(BaseResponseBean t);

        //解析年电量http response
        void analysisMonthElectResponseBean(BaseResponseBean t);

    }

    interface TotalElectView extends BaseView {
        //解析总电量http response
        void analysisTotalElectResponseBean(BaseResponseBean t);
    }

    interface Presenter extends BasePresenter {
        //control device
        void dayElect(String access_token, String deviceId, String day_end_hour,
                      String day_start_hour, String end_day,
                      String start_day);

        void monthElect(String access_token, String deviceId, String day_end_hour,
                        String day_start_hour, String end_year_month,
                        String start_year_month);

        void totalElect(String access_token, String deviceId);
    }
}
