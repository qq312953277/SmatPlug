package com.phicomm.smartplug.modules.device.devicedetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.event.PowerEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.device.devicedetails.fragment.MonthStaticsFragment;
import com.phicomm.smartplug.modules.device.devicedetails.fragment.YearStaticsFragment;
import com.phicomm.smartplug.modules.mainpage.MyFragmentAdapter;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.CustomViewPager;
import com.phicomm.widgets.PhiTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/7/17.
 */

public class PowerStatisticsActivity extends BaseActivity implements PowerStaticsContract.View {

    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.viewpager)
    CustomViewPager mViewPager;

    private ArrayList<Fragment> mList;

    private MyFragmentAdapter mPageAdapter;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    private String mDeviceNameString = "";
    private String mDeviceId = "";
    private int mRecordThreadCount = 0;

    private PowerStaticsContract.Presenter mPowerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_statistics_layout);
        initTitleBar();
        initData();
        initViewPager();
        mPowerPresenter = new PowerStatisticsPresenter(this);
        getElectLogic();
    }

    private void initData() {
        mDeviceNameString = getIntent().getStringExtra("deviceName");
        mDeviceId = getIntent().getStringExtra("deviceId");
    }

    private void initTitleBar() {
        TitlebarUtils.initTitleBar(this, R.string.power_statistics);
    }

    private void initViewPager() {
        mList = new ArrayList<>();
        MonthStaticsFragment monthFragment = new MonthStaticsFragment();
        YearStaticsFragment yearFragment = new YearStaticsFragment();
        mList.add(monthFragment);
        mList.add(yearFragment);
        mPageAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mList);
        mViewPager.setOffscreenPageLimit(mPageAdapter.getCount());
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setNoScroll(true);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn_1:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.radio_btn_2:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 获取设备电量统计
     */
    private void getElectLogic() {
        DialogUtils.showLoadingDialog(this);
        getDayElectData();
        getMonthElectData();
    }

    private void getDayElectData() {
        String access_token = DataRepository.getInstance().getAccessToken();
        mPowerPresenter.dayElect(access_token, mDeviceId, "", "", "", "");
    }

    private void getMonthElectData() {
        String access_token = DataRepository.getInstance().getAccessToken();
        mPowerPresenter.monthElect(access_token, mDeviceId, "", "", "", "");
    }

    /**
     * 本月电量统计
     *
     * @param t
     */
    @Override
    public void analysisDayElectResponseBean(BaseResponseBean t) {
        mRecordThreadCount += 1;
        if (mRecordThreadCount >= 2) {
            mRecordThreadCount = 0;
            DialogUtils.cancelLoadingDialog();
        }
        if (t.error.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            bean.setType(AppConstants.MONTH_ELECT_TYPE);
            EventBus.getDefault().post(new PowerEvent(bean));
        }
    }

    /**
     * 本年电量统计
     *
     * @param t
     */
    @Override
    public void analysisMonthElectResponseBean(BaseResponseBean t) {
        mRecordThreadCount += 1;
        if (mRecordThreadCount >= 2) {
            mRecordThreadCount = 0;
            DialogUtils.cancelLoadingDialog();
        }
        if (t.error.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            bean.setType(AppConstants.YEAR_ELECT_TYPE);
            EventBus.getDefault().post(new PowerEvent(bean));
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
