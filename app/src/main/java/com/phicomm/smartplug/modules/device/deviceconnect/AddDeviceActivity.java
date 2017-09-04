package com.phicomm.smartplug.modules.device.deviceconnect;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.event.DeviceListEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.device.devicemodify.ModifyDeviceContract;
import com.phicomm.smartplug.modules.device.devicemodify.ModifyDevicePresenter;
import com.phicomm.smartplug.modules.device.listener.SoftKeyBoardListener;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.CustomEditText;
import com.phicomm.widgets.PhiTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/7/12.
 * 添加设备
 */

public class AddDeviceActivity extends BaseActivity implements ModifyDeviceNameContract.View, ModifyDeviceContract.View,
        CustomEditText.LimitCallback {
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.device_logo)
    ImageView mDeviceLogo;

    @BindView(R.id.device_name)
    TextView mDeviceName;

    @BindView(R.id.name_edittext)
    CustomEditText mNameEditText;

    @BindView(R.id.name_edit_btn)
    LinearLayout mNameEditBtn;

    @BindView(R.id.sure_btn)
    TextView mSurebtn;

    @BindView(R.id.ok_btn)
    Button mOkBtn;

    private int mDeviceLogoHeight;

    private String mDeviceDefaultName = "";
    private String mCurrentDeviceId = "";
    private String mac = "";
    private ModifyDeviceNameContract.Presenter mModifyNamePresenter;
    private ModifyDeviceContract.Presenter mPreserter;
    private final String TAG = "AddDeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_layout);
        initView();
        initDeviceName();
        setListener();
        mModifyNamePresenter = new ModifyDeviceNamePresenter(this);
        mPreserter = new ModifyDevicePresenter(this);
        saveMac();
    }


    private void initView() {
        mDeviceDefaultName = getIntent().getStringExtra("device_name");
        mCurrentDeviceId = getIntent().getStringExtra("device_id");
        mac = getIntent().getStringExtra("mac");
        TitlebarUtils.initTitleBar(this, R.string.add_device_message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回到首页
                //send logout event
                EventBus.getDefault().post(new DeviceListEvent());
                BaseApplication.getApplication().finishActivity("ChooseWifiActivity");
                BaseApplication.getApplication().finishActivity("ChooseDeviceTipsActivity");
                finish();
            }
        });
        setActivityImmersive();
    }

    /**
     * 适配5.0以下手机
     */
    private void setActivityImmersive() {
        if (Build.VERSION.SDK_INT < 21) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    private void initDeviceName() {
        if (!StringUtils.isNull(mDeviceDefaultName)) {
            mDeviceName.setText(mDeviceDefaultName);
            mNameEditText.setText(mDeviceDefaultName);
        } else {
            mDeviceName.setText("WiFi智能插座");
            mNameEditText.setText("WiFi智能插座");
        }
        mNameEditText.setEnabled(false);
        mNameEditText.setCallback(this);
    }

    private void setListener() {

        mNameEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameEditBtn.setVisibility(View.GONE);
                mSurebtn.setVisibility(View.VISIBLE);
                mNameEditText.setEnabled(true);
                CommonUtils.openKeyboard(AddDeviceActivity.this);
                if (mNameEditText.getText().length() > 0)
                    mNameEditText.setSelection(mNameEditText.getText().length());
            }
        });
        mSurebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNameEditText.getText().length() == 0 || StringUtils.isNull(mNameEditText.getText().toString())) {
                    CommonUtils.showShortToast(getString(R.string.device_name_isNull));
                    mNameEditText.setText(mDeviceDefaultName);
                    mNameEditText.setSelection(mNameEditText.getText().length());
                    return;
                }
                //请求修改设备名称接口
                modifyDeviceName();
            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回到首页
                //send logout event
                EventBus.getDefault().post(new DeviceListEvent());
                BaseApplication.getApplication().finishActivity("ChooseWifiActivity");
                BaseApplication.getApplication().finishActivity("ChooseDeviceTipsActivity");
                finish();
            }
        });

        setKeyBoardListener();
    }

    /**
     * 修改设备名称
     */
    private void modifyDeviceName() {
        DialogUtils.showLoadingDialog(this);
        String access_token = DataRepository.getInstance().getAccessToken();
        String newDeviceName = mNameEditText.getText().toString();
        mModifyNamePresenter.modifyDeviceName(access_token, mCurrentDeviceId, newDeviceName);
        recordModifyDeviceNameEvent();
    }

    /**
     * 记录修改设备名字事件
     */
    private void recordModifyDeviceNameEvent() {
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                UmengEventValueConfig.TYPE, mCurrentDeviceId + "_" + UmengEventValueConfig.MODIFY_DEVICE_NAME);
    }

    /**
     * 保存mac地址
     */
    private void saveMac() {
        String access_token = DataRepository.getInstance().getAccessToken();
        Gson gson = new Gson();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("mac", mac);
        String attributes = gson.toJson(hashMap);
        mPreserter.modifyDevice(access_token, attributes, mCurrentDeviceId);
    }

    @Override
    public void analysisResponseBean(BaseResponseBean bean) {
        if ("0".equals(bean.error)) {
            CommonUtils.showShortToast(getResources().getString(R.string.modify_success));
            mNameEditBtn.setVisibility(View.VISIBLE);
            mSurebtn.setVisibility(View.GONE);
            mNameEditText.setEnabled(false);
            mDeviceName.setText(mNameEditText.getText().toString());
            updateDeviceName(mCurrentDeviceId, mNameEditText.getText().toString());
            mDeviceDefaultName = mNameEditText.getText().toString();
            CommonUtils.closeKeyboard(AddDeviceActivity.this);
        }
        DialogUtils.cancelLoadingDialog();
    }

    /**
     * 更新本地device name
     *
     * @param deviceId
     * @param deviceName
     */
    private void updateDeviceName(String deviceId, String deviceName) {
        DataRepository.getInstance().update(deviceId, deviceName);
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

    @Override
    public void onBackPressed() {
        //回到首页
        //send logout event
        EventBus.getDefault().post(new DeviceListEvent());
        BaseApplication.getApplication().finishActivity("ChooseWifiActivity");
        BaseApplication.getApplication().finishActivity("ChooseDeviceTipsActivity");
        super.onBackPressed();
    }

    @Override
    public void analysisModifyResponseBean(BaseResponseBean t) {
        if (t instanceof DeviceResultBean) {
            //保存成功
            DeviceResultBean bean = (DeviceResultBean) t;
            if (bean.error.equals("0")) {
                LogUtils.v(TAG, "mac save success");
            } else {
                LogUtils.v(TAG, "mac save error and " + bean.error);
            }
        }
    }

    @Override
    public void limitCallback() {
        CommonUtils.showShortToast(getString(R.string.max_device_name));
    }

    /**
     * 软键盘监听
     */
    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mDeviceLogoHeight = mDeviceLogo.getHeight();
                ViewGroup.LayoutParams params = mDeviceLogo.getLayoutParams();
                params.height = 300;
                mDeviceLogo.setLayoutParams(params);
            }

            @Override
            public void keyBoardHide(int height) {
                ViewGroup.LayoutParams params = mDeviceLogo.getLayoutParams();
                params.height = mDeviceLogoHeight;
                mDeviceLogo.setLayoutParams(params);

            }
        });
    }


}
