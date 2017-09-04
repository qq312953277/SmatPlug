package com.phicomm.smartplug.modules.device.devicedetails.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.event.PowerEvent;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.PowerStaticsBean;
import com.phicomm.smartplug.modules.device.devicedetails.view.CustomLineChartView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/7/17.
 */

public class MonthStaticsFragment extends BaseFragment {

    @BindView(R.id.lvc_main)
    CustomLineChartView mLineChartView;
    @BindView(R.id.current_date)
    TextView mCurrentDate;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_month_statics_layout, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LineChartManager.getInstance().initLineChart(null, mLineChartView);
    }

    /**
     * 获取电量信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDataList(PowerEvent event) {
        DeviceStatusBean bean = event.bean;
        if (bean.getType() == AppConstants.MONTH_ELECT_TYPE) {
            ArrayList<PowerStaticsBean> dayList = bean.respData.dayList;
            setCurrentData(dayList);
            LineChartManager.getInstance().initLineChart(dayList, mLineChartView);
        }
    }

    private void setCurrentData(ArrayList<PowerStaticsBean> dayList) {
        String startTime = dayList.get(0).oneDay;
        String endTime = dayList.get(dayList.size() - 1).oneDay;
        mCurrentDate.setText(getSubString(startTime) + "--" + getSubString(endTime));
    }

    public static String getSubString(String string) {
        return string.substring(5, string.length());
    }

}
