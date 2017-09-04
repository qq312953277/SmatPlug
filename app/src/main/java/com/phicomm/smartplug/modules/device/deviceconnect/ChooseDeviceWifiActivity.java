package com.phicomm.smartplug.modules.device.deviceconnect;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.device.BindDeviceBean;
import com.phicomm.smartplug.modules.personal.commonissues.CommonIssuesActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.CountDownUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by yangf on 2017/7/5.
 * 选择设备wifi
 */

public class ChooseDeviceWifiActivity extends BaseActivity implements BindDeviceContract.View {
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.tips_txt)
    LinearLayout mTipsTxt;

    @BindView(R.id.device_logo)
    LinearLayout mDeviceLogo;

    @BindView(R.id.current_ssid)
    TextView mCurrentSsidText;

    @BindView(R.id.switch_btn)
    Button mSwitchBtn;

    @BindView(R.id.loading_view)
    RelativeLayout mScanningView;

    @BindView(R.id.connect_device_tips)
    TextView mConnectTips;

    private ImageView mLoadImage;
    private TextView mLoadTime;

    private String mCurrentSsid = "";
    private String mCurrentPassword = "";
    private WifiChangeReceiver mWifiChangeReceiver;

    //倒计时
    public static final int TOTAL_COUNT = 60;
    private BindDeviceContract.Presenter mBindPresenter;

    //是否已经连接上设备
    private boolean isConnectedDevice = false;
    private String mData = "";
    private CountDownUtils mCountDown;
    private boolean isCanBackPressed = true;
    private final String TAG = "ChooseDeviceWifiActivity";

    private PhiTitleBar.Action mHelpTis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device_wifi_layout);
        registerWifiChangeReceiver();
        initView();
    }

    private void initView() {
        isCanBackPressed = true;
        mBindPresenter = new BindDevicePresenter(this);
        mCurrentSsid = getIntent().getStringExtra("ssid");
        mCurrentPassword = getIntent().getStringExtra("password");
        mCurrentSsidText.setText(getString(R.string.current_wifi) + " : " + mCurrentSsid);
        TitlebarUtils.initTitleBar(this, R.string.choose_device_wifi);
        mHelpTis = new PhiTitleBar.ImageAction(R.drawable.help_tips_icon) {
            @Override
            public void performAction(View view) {
                startActivityClearTop(null, CommonIssuesActivity.class);
            }
        };
        mTitleBar.addAction(mHelpTis);
        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.WIFI_SETTINGS");
                startActivity(intent);
            }
        });
        mLoadImage = (ImageView) mScanningView.findViewById(R.id.image);
        mLoadTime = (TextView) mScanningView.findViewById(R.id.time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiChangeReceiver);
        if (null != mCountDown) {
            mCountDown.stopTimer();
        }
    }

    private void registerWifiChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mWifiChangeReceiver = new WifiChangeReceiver();
        registerReceiver(mWifiChangeReceiver, filter);
    }

    @Override
    public void analysisResponseBean(BaseResponseBean bean) {
        if (bean.error.equals("0")) {
            //请求成功
            if (bean instanceof BindDeviceBean) {
                BindDeviceBean bindDeviceBean = (BindDeviceBean) bean;
                String deviceID = bindDeviceBean.result.deviceID;
                String deviceName = bindDeviceBean.result.name;
                String mac = bindDeviceBean.result.mac;
                Intent extras = new Intent();
                extras.putExtra("device_id", deviceID);
                extras.putExtra("device_name", deviceName);
                extras.putExtra("mac", mac);
                if (null != animator) {
                    animator.cancel();
                    animator = null;
                }
                if (null != mCountDown) {
                    mCountDown.stopTimer();
                }
                recordBindEvent(true, mac);
                startActivityClearTopAndFinishSelf(extras, AddDeviceActivity.class);
            }
        } else {
            if (bean.error.equals("103")) {
                LogUtils.v(TAG, getString(R.string.not_persistent_connection));
            }
            bindDevice(mData);
            String mac = getDeviceMac(mData);
            recordBindEvent(false, mac);
        }
    }

    /**
     * 记录设备绑定事件
     *
     * @param result
     * @param mac
     */
    private void recordBindEvent(boolean result, String mac) {
        if (result) {
            DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                    UmengEventValueConfig.TYPE, mac + "_" + UmengEventValueConfig.BIND_SUCCESS);
        } else {
            DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                    UmengEventValueConfig.TYPE, mac + "_" + UmengEventValueConfig.BIND_FAILED);
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

    class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //用于判断是否连接到了有效wifi（不能用于判断是否能够连接互联网）
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                        String ssid = wifiInfo.getSSID();
                        String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
                        if (ssid.contains(AppConstants.DEVICE_WIFI_TAG) && !StringUtils.isNull(bssid)) {
                            //1.修改界面
                            refreshSearchView();
                            //2.开始执行基于UDP的Socket通信
                            udpCommunicate();
                        } else if (!StringUtils.isNull(bssid)) {
                            //正常情况进入
                            //获取到data，然后去执行绑定设备网络请求
                            if (isConnectedDevice && !StringUtils.isNull(mData)) {
                                UdpClientConnector.getInstance().setUdpFinished(true);
                                bindDevice(mData);
                            }
                        }
                    }
                }
            }
        }
    }

    private void refreshSearchView() {
        isCanBackPressed = false;
        TitlebarUtils.initTitleBar(this, R.string.try_connecting_device);
        mTitleBar.setLeftVisible(false);
        if (null != mHelpTis) {
            mTitleBar.removeAction(mHelpTis);
        }
        mDeviceLogo.setVisibility(View.GONE);
        mTipsTxt.setVisibility(View.GONE);
        mSwitchBtn.setVisibility(View.GONE);
        mCurrentSsidText.setVisibility(View.GONE);
        mScanningView.setVisibility(View.VISIBLE);
        mConnectTips.setVisibility(View.VISIBLE);
        showDeviceLoadingAnim();
        startTimerTask();
    }

    /**
     * 加载loading动画
     */
    private ObjectAnimator animator;

    private void showDeviceLoadingAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animator = ObjectAnimator.ofFloat(mLoadImage, "rotation", 0f, 359f);
                animator.setRepeatCount(ObjectAnimator.INFINITE);
                animator.setDuration(1500);
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
            }
        }, 300);
    }

    /**
     * 计时60s
     */
    public void startTimerTask() {
        if (null == mCountDown)
            mCountDown = new CountDownUtils(timerHandler);
        mCountDown.setRestorTime(TOTAL_COUNT);
        mCountDown.startTimerTask();
    }

    Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CountDownUtils.START_TIMER:
                    long second = (Long) msg.obj;
                    String showText = String.format("%ss", TOTAL_COUNT - second);
                    mLoadTime.setText(showText);
                    break;
                case CountDownUtils.STOP_TIMER:
                    //设备绑定失败
                    UdpClientConnector.getInstance().setUdpFinished(true);
                    CommonUtils.showShortToast(getString(R.string.bind_failed));
                    if (null != animator) {
                        animator.cancel();
                        animator = null;
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void udpCommunicate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("header", "phi-plug-0001");
            jsonObject.put("uuid", "00010");
            jsonObject.put("action", "wifi=");
            jsonObject.put("auth", "");
            JSONObject params = new JSONObject();
            params.put("ssid", mCurrentSsid);
            params.put("password", mCurrentPassword);
            jsonObject.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = jsonObject.toString();
        message = message + "\n"; //一定要加"\n"
        UdpClientConnector.getInstance().createConnector(message, new UdpClientConnector.ConnectListener() {
            @Override
            public void onReceiveData(String data) {
                isConnectedDevice = true;
                //获取到data
                mData = data;
                UdpClientConnector.getInstance().send();
            }

            @Override
            public void onSoTimeout() {
                UdpClientConnector.getInstance().send();
            }
        });
    }

    private void bindDevice(String data) {
        final String access_token = DataRepository.getInstance().getAccessToken();
        final String mac = getDeviceMac(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkManagerUtils.isNetworkAvailable(ChooseDeviceWifiActivity.this)) {
                    mBindPresenter.bindDevice(access_token, mac);
                }
            }
        }, 2000);
    }

    private String getDeviceMac(String data) {
        String mac = "";
        try {
            JSONObject json = new JSONObject(data);
            if (json.optInt("status") == 200) {
                JSONObject jsonObject = json.optJSONObject("result");
                mac = jsonObject.optString("mac");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mac;
    }

    @Override
    public void onBackPressed() {
        if (isCanBackPressed) {
            super.onBackPressed();
        } else {
            CommonUtils.showShortToast(getString(R.string.exit_scan_device_tips));
        }
    }
}
