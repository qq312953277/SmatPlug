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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by feilong.yang on 2017/7/17.
 */

public class YearStaticsFragment extends BaseFragment {

    @BindView(R.id.ccv_main)
    ColumnChartView mColumnCharView;
    @BindView(R.id.current_year)
    TextView mCurrentYear;
    @BindView(R.id.prower_tips)
    TextView mProwerTips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year_statics_layout, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        ColumnChartManager.getInstance().initColumnChart(null, mColumnCharView);
    }

    private void initView() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        mCurrentYear.setText(year + "");
        mProwerTips.setText(year + getString(R.string.consumption_record));
    }

    /**
     * 获取电量信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDataList(PowerEvent event) {
        DeviceStatusBean bean = event.bean;
        if (bean.getType() == AppConstants.YEAR_ELECT_TYPE) {
            ArrayList<PowerStaticsBean> monthList = bean.respData.monthList;
            ColumnChartManager.getInstance().initColumnChart(monthList, mColumnCharView);
        }
    }

}
