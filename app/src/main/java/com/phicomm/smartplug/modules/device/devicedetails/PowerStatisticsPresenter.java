package com.phicomm.smartplug.modules.device.devicedetails;

import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

/**
 * Created by feilong.yang on 2017/7/13.
 */

public class PowerStatisticsPresenter implements PowerStaticsContract.Presenter {

    private PowerStaticsContract.View mView;

    private PowerStaticsContract.TotalElectView mTotalView;

    public PowerStatisticsPresenter(PowerStaticsContract.View view) {
        mView = view;
    }

    public PowerStatisticsPresenter(PowerStaticsContract.TotalElectView view) {
        mTotalView = view;
    }

    /**
     * 获取设备月消耗电量
     */
    @Override
    public void dayElect(String access_token, String deviceId, String day_end_hour,
                         String day_start_hour, String end_day,
                         String start_day) {
        DataRepository.getInstance().dayElect(mView.getRxLifeCycleObj(), new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mView.analysisDayElectResponseBean(deviceStatusBean);
            }
        }, access_token, deviceId, day_end_hour, day_start_hour, end_day, start_day);
    }

    /**
     * 获取设备年消耗电量
     */
    @Override
    public void monthElect(String access_token, String deviceId, String day_end_hour,
                           String day_start_hour, String end_year_month,
                           String start_year_month) {
        DataRepository.getInstance().monthElect(mView.getRxLifeCycleObj(), new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mView.analysisMonthElectResponseBean(deviceStatusBean);
            }
        }, access_token, deviceId, day_end_hour, day_start_hour, end_year_month, start_year_month);
    }

    /**
     * 获取设备累计用电总量
     */
    @Override
    public void totalElect(String access_token, String deviceId) {
        DataRepository.getInstance().totalElect(mTotalView.getRxLifeCycleObj(), access_token, new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mTotalView.analysisTotalElectResponseBean(deviceStatusBean);
            }
        }, deviceId);
    }
}
