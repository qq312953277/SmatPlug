package com.phicomm.smartplug.modules.device.deviceconnect;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.utils.WifiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yangf on 2017/7/4.
 * 选择设备工作wifi
 */

public class ChooseWifiActivity extends BaseActivity {

    @BindView(R.id.change_btn)
    TextView mChangeBtn;

    @BindView(R.id.ssid)
    EditText mSsid;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.next_btn)
    Button mNextBtn;

    @BindView(R.id.iv_eye)
    ToggleButton mPasswordEye;

    @BindView(R.id.iv_eye_layout)
    LinearLayout mPasswordEyeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_wifi_layout);
        initView();
    }

    private void initView() {

        TitlebarUtils.initTitleBar(this, R.string.choose_wifi, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWifiInfo(mSsid.getText().toString(), mPassword.getText().toString());
                finish();
            }
        });

        mPasswordEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //显示密码
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // 隐藏密码
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPassword.setSelection(mPassword.length());
            }
        });

        mPasswordEyeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordEye.isChecked()) {
                    mPasswordEye.setChecked(false);
                } else {
                    mPasswordEye.setChecked(true);
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWifiInfo(mSsid.getText().toString(), mPassword.getText().toString());
                Intent extras = new Intent();
                extras.putExtra("ssid", mSsid.getText().toString());
                extras.putExtra("password", mPassword.getText().toString());
                startActivityClearTop(extras, ChooseDeviceWifiActivity.class);
                BaseApplication.getApplication().addActivity(TAG, ChooseWifiActivity.this);
            }
        });
    }

    @OnClick(R.id.change_btn)
    public void clickChangeBtn() {
        //跳转到系统设置Wi-Fi的界面
        Intent intent = new Intent();
        intent.setAction("android.settings.WIFI_SETTINGS");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWifi();
    }

    /**
     * 显示连接的Wi-Fi信息
     */
    public void showWifi() {
        if (NetworkManagerUtils.instance().isDataWIFIUp()) {
            String ssid = WifiUtils.getCurrentSSID(this);
            String password = getWifiPassword(ssid);
            if (!StringUtils.isNull(ssid)) {
                mSsid.setText(ssid);
            }
            if (!StringUtils.isNull(password)) {
                mPassword.setText(password);
                mPassword.setSelection(password.length());
            } else {
                mPassword.setText(null);
            }
            mNextBtn.setEnabled(true);
        } else {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int stringResId;
            if (wifiManager.isWifiEnabled()) {
                stringResId = R.string.onboard_warning3;
            } else {
                stringResId = R.string.onboard_warning2;
            }

            mSsid.setText("");
            mNextBtn.setEnabled(false);
            CommonUtils.showShortToast(getResources().getString(stringResId));
        }
    }

    /**
     * 保存wifi信息
     *
     * @param wifi
     * @param password
     */
    private void saveWifiInfo(String wifi, String password) {
        DataRepository.getInstance().setWifiPassword(wifi, password);
    }

    /**
     * 获取wifi密码
     *
     * @param ssid
     * @return
     */
    private String getWifiPassword(String ssid) {
        return DataRepository.getInstance().getWifiPassword(ssid);
    }

    @Override
    public void onBackPressed() {
        saveWifiInfo(mSsid.getText().toString(), mPassword.getText().toString());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getApplication().removeActivity(TAG);
    }
}
