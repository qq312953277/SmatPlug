package com.phicomm.smartplug.modules.device.devicedetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.event.ModifyDeviceEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceAttributesBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.ResultBean;
import com.phicomm.smartplug.modules.device.deviceconnect.UmengEventValueConfig;
import com.phicomm.smartplug.modules.device.devicemodify.ModifyDeviceActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.CompoundButtonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.OfflineLoadingUtil;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/7/13.
 * 设备详情页
 */

public class DeviceDetailsActivity extends BaseActivity implements PowerStaticsContract.TotalElectView,
        DeviceDetailContract.View, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.power_info)
    LinearLayout mPowerInfoLayout;

    private DeviceDetailContract.Presenter mDevicePresenter;
    private String mDeviceId = "";
    private String mDeviceName = "";
    private boolean isOnline = true;

    private DeviceAttributesBean mAttributesBean;//设备插口名称
    @BindView(R.id.switch_main)
    TextView mSwitchMain;

    @BindView(R.id.switch_1)
    TextView mSwitch1;

    @BindView(R.id.switch_2)
    TextView mSwitch2;

    @BindView(R.id.switch_3)
    TextView mSwitch3;

    @BindView(R.id.button1)
    ToggleButton mButton1;

    @BindView(R.id.button2)
    ToggleButton mButton2;

    @BindView(R.id.button3)
    ToggleButton mButton3;

    @BindView(R.id.button4)
    ToggleButton mButton4;

    //当前电压
    @BindView(R.id.current_vol)
    TextView mCurrentVol;

    //当前电流
    @BindView(R.id.current_cur)
    TextView mCurrentCur;

    //当前功率
    @BindView(R.id.current_power)
    TextView mCurrentPower;

    //额定功率
    @BindView(R.id.rated_power)
    TextView mRatedPower;

    //累计用电
    @BindView(R.id.total_elec)
    TextView mTotalElec;

    @BindView(R.id.device_tips)
    TextView deviceTips;

    private Timer mTimer;

    private String mStatusCode = "";

    //标记开关改变是处于手动控制还是详情刷新
    private int BUTTON_CHANGE_MODE = -1;

    private boolean interrupted = false;

    //设置当前开关是处于手动控制还是详情刷新
    private void setChangetMode(int mode) {
        BUTTON_CHANGE_MODE = mode;
    }

    private PowerStaticsContract.Presenter mPowerPresenter;

    private final int TOTAL_ELEC = 2500;
    private static final long CLICK_LIMIT_TIME = 800L;
    private long NORMAL_CLICK_TIME = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details_layout);
        initView();
    }

    private void initView() {
        mAttributesBean = (DeviceAttributesBean) getIntent().getSerializableExtra("attributes");
        mDeviceId = getIntent().getStringExtra("deviceId");
        mDeviceName = getIntent().getStringExtra("deviceName");
        isOnline = getIntent().getBooleanExtra("isOnline", true);
        mDevicePresenter = new DeviceDetailPresenter(this);
        mPowerPresenter = new PowerStatisticsPresenter(this);
        mRatedPower.setText(TOTAL_ELEC + "W");
        initTitleBar();
        initSwitchName();
        setListener();
        initOfflineView();
        //累计用电量不需要在onResume中反复调用
        totalElect();
    }

    private void totalElect() {
        if (NetworkManagerUtils.isNetworkAvailable(this)) {
            String access_token = DataRepository.getInstance().getAccessToken();
            mPowerPresenter.totalElect(access_token, mDeviceId);
        } else {
            CommonUtils.showShortToast(getString(R.string.network_unavailable));
        }
    }

    private void initOfflineView() {
        if (isOnline) {
            DialogUtils.showLoadingDialog(this);
        } else {
            OfflineLoadingUtil.showLoadingDialog(this);
        }
    }

    /**
     * 设置插口的名称
     */
    private void initSwitchName() {
        if (null != mAttributesBean) {
            if (!StringUtils.isNull(mAttributesBean.getSwitchMainName()))
                mSwitchMain.setText(mAttributesBean.getSwitchMainName());
            if (!StringUtils.isNull(mAttributesBean.getSwitch1Name()))
                mSwitch1.setText(mAttributesBean.getSwitch1Name());
            if (!StringUtils.isNull(mAttributesBean.getSwitch2Name()))
                mSwitch2.setText(mAttributesBean.getSwitch2Name());
            if (!StringUtils.isNull(mAttributesBean.getSwitch3Name()))
                mSwitch3.setText(mAttributesBean.getSwitch3Name());
        }
    }

    private void initTitleBar() {
        if (!StringUtils.isNull(mDeviceName)) {
            TitlebarUtils.initTitleBar(this, mDeviceName);
        } else {
            TitlebarUtils.initTitleBar(this, R.string.smart_plug_board);
        }
        //设置titlebar右侧图标
        mTitleBar.addAction(new PhiTitleBar.ImageAction(R.drawable.device_info_edit_icon) {
            @Override
            public void performAction(View view) {
                if (!NetworkManagerUtils.isNetworkAvailable(DeviceDetailsActivity.this)) {
                    CommonUtils.showShortToast(getString(R.string.network_unavailable));
                    return;
                }
                //编辑
                Intent extras = new Intent();
                extras.putExtra("deviceName", mDeviceName);
                extras.putExtra("attributes", mAttributesBean);
                extras.putExtra("deviceId", mDeviceId);
                startActivityClearTop(extras, ModifyDeviceActivity.class);
                BaseApplication.getApplication().addActivity(TAG, DeviceDetailsActivity.this);
            }
        });
    }

    private void setListener() {
        mPowerInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkManagerUtils.isNetworkAvailable(DeviceDetailsActivity.this)) {
                    CommonUtils.showShortToast(getString(R.string.network_unavailable));
                    return;
                }
                Intent extras = new Intent();
                extras.putExtra("deviceName", mDeviceName);
                extras.putExtra("deviceId", mDeviceId);
                startActivityClearTop(extras, PowerStatisticsActivity.class);
            }
        });
        mButton1.setOnCheckedChangeListener(this);
        mButton2.setOnCheckedChangeListener(this);
        mButton3.setOnCheckedChangeListener(this);
        mButton4.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取设备详情
        if (null == mTimer) {
            mTimer = new Timer();
        } else {
            mTimer.cancel();
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getDeviceControl(AppConstants.DEVICE_INFO, -1);
            }
        }, 10, 20000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTimer) {
            mTimer.cancel();
        }
        BaseApplication.getApplication().removeActivity(TAG);
        CompoundButtonUtils.getInstace().removeButtonView();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        recordOperation(buttonView);

        if (getChangeButtonStatus()) {
            return;
        }

        if (BUTTON_CHANGE_MODE != AppConstants.DEVICE_INFO) {
            setDeviceControl(buttonView);
        }
    }

    /**
     * 实际控制逻辑
     */
    private void setDeviceControl(CompoundButton buttonView) {

        if (!NetworkManagerUtils.isNetworkAvailable(this)) {
            CommonUtils.showShortToast(getString(R.string.network_unavailable));
            return;
        }

        long now = System.currentTimeMillis();
        if (now - NORMAL_CLICK_TIME <= CLICK_LIMIT_TIME) {
            resetButtonStatus();
            LogUtils.v(TAG, "Click too fast");
            return;
        }
        NORMAL_CLICK_TIME = now;
        if (buttonView.getId() == R.id.button1) {
            initSwitchStatus(buttonView.isChecked());
        }
        int switchMainStatus = mButton1.isChecked() ? 1 : 0; //总开关 个
        int switch1status = mButton2.isChecked() ? 1 : 0;  //插口1  十
        int switch2status = mButton3.isChecked() ? 1 : 0;  //插口2  百
        int switch3status = mButton4.isChecked() ? 1 : 0; //插口3   千
        int status = switch3status * 1000 + switch2status * 100 + switch1status * 10 + switchMainStatus;
        DialogUtils.showLoadingDialog(this);
        getDeviceControl(AppConstants.DEVICE_CONTROL, status);
        String value = getEventValue(buttonView);
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE_CONTROL,
                UmengEventValueConfig.TYPE, value);
    }

    /**
     * @param type   区分是详情还是控制接口
     * @param status 如果是详情接口status随便传，默认传-1
     */
    private void getDeviceControl(int type, int status) {
        String access_token = DataRepository.getInstance().getAccessToken();
        if (type == AppConstants.DEVICE_CONTROL) {
            mDevicePresenter.setDeviceControl(type, status, mDeviceId, access_token);
        } else {
            mDevicePresenter.getDeviceInfo(type, status, mDeviceId, access_token);
        }
    }

    private boolean getChangeButtonStatus() {
        //网络不可用
        if (!NetworkManagerUtils.isNetworkAvailable(this)) {
            CommonUtils.showShortToast(getString(R.string.network_unavailable));
            resetButtonStatus();
            return true;
        }
        //设备无法通信
        if (!StringUtils.isNull(mStatusCode) && mStatusCode.equals("102")) {
            if (interrupted) {
                return true;
            }
            CommonUtils.showShortToast(getString(R.string.device_cannot_communicate));
            resetButtonStatus();
            return true;
        }

        //设备离线
        if (!StringUtils.isNull(mStatusCode) && mStatusCode.equals("103")) {
            if (interrupted) {
                return true;
            }
            CommonUtils.showShortToast(getString(R.string.device_offline));
            resetButtonStatus();
            return true;
        }

        //设备跳闸，控制失败
        if (!StringUtils.isNull(mStatusCode) && (mStatusCode.equals("400") || mStatusCode.equals("212"))) {
            if (interrupted) {
                return true;
            }
            CommonUtils.showShortToast(getString(R.string.get_switch_status));
            resetButtonStatus();
            return true;
        }
        return false;
    }

    /**
     * 记录开关控制事件
     *
     * @param buttonView
     * @return
     */
    private String getEventValue(CompoundButton buttonView) {
        String event = "";
        switch (buttonView.getId()) {
            case R.id.button1:
                if (buttonView.isChecked()) {
                    event = UmengEventValueConfig.MASTER_SWITCH_ON;
                } else {
                    event = UmengEventValueConfig.MASTER_SWITCH_OFF;
                }
                break;
            case R.id.button2:
                if (buttonView.isChecked()) {
                    event = UmengEventValueConfig.SWITCH1_ON;
                } else {
                    event = UmengEventValueConfig.SWITCH1_OFF;
                }
                break;
            case R.id.button3:
                if (buttonView.isChecked()) {
                    event = UmengEventValueConfig.SWITCH2_ON;
                } else {
                    event = UmengEventValueConfig.SWITCH2_OFF;
                }
                break;
            case R.id.button4:
                if (buttonView.isChecked()) {
                    event = UmengEventValueConfig.SWITCH3_ON;
                } else {
                    event = UmengEventValueConfig.SWITCH3_OFF;
                }
                break;
            default:
                break;
        }
        return event;
    }

    /**
     * 设备详情
     *
     * @param t
     */
    @Override
    public void analysisInfoResponseBean(BaseResponseBean t) {
        DialogUtils.cancelLoadingDialog();
        mStatusCode = t.error;
        if (mStatusCode.equals("0") && t instanceof DeviceStatusBean) {
            setChangetMode(AppConstants.DEVICE_INFO);
            DeviceStatusBean bean = (DeviceStatusBean) t;
            refreshSwitchInfo(bean);
        } else if (mStatusCode.equals("102")) {
            CommonUtils.showShortToast(getString(R.string.device_cannot_communicate));
        }
        setChangetMode(-1);
    }

    private void refreshSwitchInfo(DeviceStatusBean bean) {
        //设备过载处理
        mStatusCode = bean.respData.status + "";
        if (bean.respData.status == 211) {
            deviceTips.setVisibility(View.VISIBLE);
            deviceTips.setText(R.string.device_tips1);
        } else if (bean.respData.status == 400 || bean.respData.status == 212) {
            deviceTips.setVisibility(View.VISIBLE);
            deviceTips.setText(R.string.device_tips2);
        } else {
            deviceTips.setVisibility(View.GONE);
        }

        ResultBean resultBean = bean.respData.result;
        int state = resultBean.status;
        int[] status = new int[4];//status[0]表示总开关，status[1]表示插口1，status[2]表示插口2，status[3]表示插口3，
        getSwitchStatus(status, state);
        mButton1.setChecked(status[0] == 1 ? true : false);
        mButton2.setChecked(status[1] == 1 ? true : false);
        mButton3.setChecked(status[2] == 1 ? true : false);
        mButton4.setChecked(status[3] == 1 ? true : false);
        initPowerInfo(resultBean);
        initSwitchStatus();
    }


    /**
     * 显示电量信息
     *
     * @param resultBean
     */
    private void initPowerInfo(ResultBean resultBean) {
        mCurrentVol.setText(resultBean.V + "V");
        DecimalFormat df = new DecimalFormat("###.####");
        mCurrentCur.setText(df.format(resultBean.I / 1000f) + "A");
        mCurrentPower.setText(resultBean.P + "W");
    }

    private void initSwitchStatus() {
        initSwitchStatus(mButton1.isChecked());
    }

    /**
     * @param isChecked 总开关的选择状态
     */
    private void initSwitchStatus(boolean isChecked) {
        if (!isChecked) {
            if (mButton2.isEnabled()) {
                mButton2.setEnabled(false);
            }
            if (mButton3.isEnabled()) {
                mButton3.setEnabled(false);
            }
            if (mButton4.isEnabled()) {
                mButton4.setEnabled(false);
            }
        } else {
            if (!mButton2.isEnabled()) {
                mButton2.setEnabled(true);
            }
            if (!mButton3.isEnabled()) {
                mButton3.setEnabled(true);
            }
            if (!mButton4.isEnabled()) {
                mButton4.setEnabled(true);
            }
        }
    }

    /**
     * 获取插口的状态码
     *
     * @param status
     * @param state
     * @return
     */
    private int[] getSwitchStatus(int[] status, int state) {
        int x1 = state;
        int i = 1, k, count = 0;
        for (i = 0; i < 4; i++) {
            if (x1 / Math.pow(10, i) != 0) {
                count++;
            }
        }
        for (k = 0; k < count; k++) {
            status[k] = x1 % 10;
            x1 = x1 / 10;
        }
        return status;
    }

    /**
     * 设备控制
     *
     * @param t
     */
    @Override
    public void analysisControlResponseBean(BaseResponseBean t) {
        DialogUtils.cancelLoadingDialog();
        mStatusCode = t.error;
        if (mStatusCode.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            if (bean.respData.status == 400 || bean.respData.status == 212) {
                mStatusCode = bean.respData.status + "";
                //设备过载处理
                CommonUtils.showShortToast(getString(R.string.get_switch_status));
                interrupted = true;
                resetButtonStatus();
                interrupted = false;
                deviceTips.setVisibility(View.VISIBLE);
                deviceTips.setText(R.string.device_tips2);
            } else {
                if (bean.respData.status == 211) {
                    deviceTips.setVisibility(View.VISIBLE);
                    deviceTips.setText(R.string.device_tips1);
                } else {
                    deviceTips.setVisibility(View.GONE);
                }
                CommonUtils.showShortToast(getString(R.string.device_setting_succ));
            }
        } else if (mStatusCode.equals("103")) {
            CommonUtils.showShortToast(getString(R.string.device_offline));
            interrupted = true;
            resetButtonStatus();
            interrupted = false;
        } else if (mStatusCode.equals("102")) {
            CommonUtils.showShortToast(getString(R.string.device_cannot_communicate));
            interrupted = true;
            resetButtonStatus();
            interrupted = false;
        }
    }

    /**
     * 获取设备总耗电量
     *
     * @param t
     */
    @Override
    public void analysisTotalElectResponseBean(BaseResponseBean t) {
        if (t.error.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            float totalElect = bean.respData.totalElect;
            if (totalElect == 0) {
                mTotalElec.setText("0" + getString(R.string.degree));
            } else {
                DecimalFormat fnum = new DecimalFormat("##0.00");
                String dd = fnum.format(totalElect);
                mTotalElec.setText(dd + getString(R.string.degree));
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshSwitchName(ModifyDeviceEvent event) {
        if (null != event.mBean && null != event.mBean.result) {
            mAttributesBean = event.mBean.result.attributes;
            initSwitchName();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    /**
     * `
     * 保持开关状态
     *
     * @param buttonView
     */
    private void recordOperation(CompoundButton buttonView) {
        CompoundButtonUtils.getInstace().recordOperation(buttonView);
    }

    /**
     * 还原开关状态
     */
    private void resetButtonStatus() {
        CompoundButtonUtils.getInstace().resetButtonStatus(new CompoundButtonUtils.ResetButtonListener() {
            @Override
            public void callback() {
                initSwitchStatus();
            }
        });
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

}
