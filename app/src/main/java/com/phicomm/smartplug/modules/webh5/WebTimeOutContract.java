package com.phicomm.smartplug.modules.webh5;

import com.phicomm.smartplug.base.BasePresenter;

/**
 * Created by yun.wang on 2017/4/24.
 */

public interface WebTimeOutContract {

    interface View {

        //show timeout layout
        void showTimeOutView();
    }

    interface Presenter extends BasePresenter {

        void initTimer();

        void startTimerSchedule();

        void destoryTimer();

    }
}
