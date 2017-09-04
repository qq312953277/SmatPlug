package com.phicomm.smartplug.modules.device.devicemodify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.event.ModifyDeviceEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceAttributesBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceResultBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.device.deviceconnect.BindDeviceContract;
import com.phicomm.smartplug.modules.device.deviceconnect.BindDevicePresenter;
import com.phicomm.smartplug.modules.device.deviceconnect.UmengEventValueConfig;
import com.phicomm.smartplug.modules.device.devicedetails.DeviceDetailContract;
import com.phicomm.smartplug.modules.device.devicedetails.DeviceDetailPresenter;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.CustomEditText;
import com.phicomm.widgets.PhiTitleBar;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/7/19.
 */

public class ModifyDeviceActivity extends BaseActivity implements View.OnClickListener, ModifyDeviceContract.View,
        DeviceDetailContract.FirmwareView, BindDeviceContract.View, CustomEditText.LimitCallback {
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.device_name)
    TextView mDeviceName;

    @BindView(R.id.master_switch_edit)
    CustomEditText mSwitchMainEdit;

    @BindView(R.id.switch1_edit)
    CustomEditText mSwitchEdit1;

    @BindView(R.id.switch2_edit)
    CustomEditText mSwitchEdit2;

    @BindView(R.id.switch3_edit)
    CustomEditText mSwitchEdit3;

    @BindView(R.id.master_switch_edit_btn)
    LinearLayout mSwitchMainEditBtn;

    @BindView(R.id.switch1_edit_btn)
    LinearLayout mSwitchEditBtn1;

    @BindView(R.id.switch2_edit_btn)
    LinearLayout mSwitchEditBtn2;

    @BindView(R.id.switch3_edit_btn)
    LinearLayout mSwitchEditBtn3;

    @BindView(R.id.master_switch_sure_btn)
    TextView mSwitchMainSureBtn;

    @BindView(R.id.switch1_sure_btn)
    TextView mSwitchSureBtn1;

    @BindView(R.id.switch2_sure_btn)
    TextView mSwitchSureBtn2;

    @BindView(R.id.switch3_sure_btn)
    TextView mSwitchSureBtn3;

    @BindView(R.id.delete_btn)
    Button mDeleteBtn;

    @BindView(R.id.mac)
    TextView mMAcAddress;

    @BindView(R.id.firmware_version)
    TextView mFirmwareViesion;

    @BindView(R.id.mac_title)
    TextView mMAcAddressTitle;

    @BindView(R.id.firmware_version_title)
    TextView mFirmwareViesionTitle;

    private String mDeviceNameString = "";
    private String mDeviceId = "";
    private DeviceAttributesBean mAttributesBean;//设备插口名称

    private ModifyDeviceContract.Presenter mPreserter;

    private BindDeviceContract.Presenter mBindPresenter;

    private DeviceDetailContract.Presenter mDevicePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_device_layout);
        initTitleBar();
        initView();
        getFirmwareVersion();
    }

    private void initTitleBar() {
        TitlebarUtils.initTitleBar(this, R.string.modify_device, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSaveDeviceName();
            }
        });
        mTitleBar.setActionTextColor(R.color.white);
        //设置titlebar右侧图标
        mTitleBar.addAction(new PhiTitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                saveAttributes();
            }
        });
    }

    private void initView() {
        mDevicePresenter = new DeviceDetailPresenter(this);
        mPreserter = new ModifyDevicePresenter(this);
        mBindPresenter = new BindDevicePresenter(this);
        mAttributesBean = (DeviceAttributesBean) getIntent().getSerializableExtra("attributes");
        mDeviceNameString = getIntent().getStringExtra("deviceName");
        mDeviceId = getIntent().getStringExtra("deviceId");
        if (!StringUtils.isNull(mDeviceNameString)) {
            mDeviceName.setText(mDeviceNameString);
        }
        setViewListener();
        setEditTextView();
        if (null != mAttributesBean && !StringUtils.isNull(mAttributesBean.getMac())) {
            mMAcAddressTitle.setVisibility(View.VISIBLE);
            mMAcAddress.setVisibility(View.VISIBLE);
            mMAcAddress.setText(mAttributesBean.getMac());
        }
    }

    private void setViewListener() {
        mSwitchMainEditBtn.setOnClickListener(this);
        mSwitchEditBtn1.setOnClickListener(this);
        mSwitchEditBtn2.setOnClickListener(this);
        mSwitchEditBtn3.setOnClickListener(this);
        mSwitchMainSureBtn.setOnClickListener(this);
        mSwitchSureBtn1.setOnClickListener(this);
        mSwitchSureBtn2.setOnClickListener(this);
        mSwitchSureBtn3.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    /**
     * 设置EditText显示
     */
    private void setEditTextView() {
        if (null != mAttributesBean) {
            if (!StringUtils.isNull(mAttributesBean.getSwitchMainName())) {
                mSwitchMainEdit.setText(mAttributesBean.getSwitchMainName());
            } else {
                mSwitchMainEdit.setText(getString(R.string.master_switch));
                mAttributesBean.setSwitchMainName(getString(R.string.master_switch));
            }

            if (!StringUtils.isNull(mAttributesBean.getSwitch1Name())) {
                mSwitchEdit1.setText(mAttributesBean.getSwitch1Name());
            } else {
                mSwitchEdit1.setText(getString(R.string.socket_1));
                mAttributesBean.setSwitch1Name(getString(R.string.socket_1));
            }
            if (!StringUtils.isNull(mAttributesBean.getSwitch2Name())) {
                mSwitchEdit2.setText(mAttributesBean.getSwitch2Name());
            } else {
                mSwitchEdit2.setText(getString(R.string.socket_2));
                mAttributesBean.setSwitch2Name(getString(R.string.socket_2));
            }
            if (!StringUtils.isNull(mAttributesBean.getSwitch3Name())) {
                mSwitchEdit3.setText(mAttributesBean.getSwitch3Name());
            } else {
                mSwitchEdit3.setText(getString(R.string.socket_3));
                mAttributesBean.setSwitch3Name(getString(R.string.socket_3));
            }
        }

        mSwitchMainEdit.setCallback(this);
        mSwitchEdit1.setCallback(this);
        mSwitchEdit2.setCallback(this);
        mSwitchEdit3.setCallback(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.master_switch_edit_btn:
                changeEditView(mSwitchMainEdit, v.getId(), R.id.master_switch_sure_btn);
                break;

            case R.id.switch1_edit_btn:
                changeEditView(mSwitchEdit1, v.getId(), R.id.switch1_sure_btn);
                break;

            case R.id.switch2_edit_btn:
                changeEditView(mSwitchEdit2, v.getId(), R.id.switch2_sure_btn);
                break;

            case R.id.switch3_edit_btn:
                changeEditView(mSwitchEdit3, v.getId(), R.id.switch3_sure_btn);
                break;

            case R.id.master_switch_sure_btn:
                changeTextView(mSwitchMainEdit, R.id.master_switch_edit_btn, v.getId());
                break;

            case R.id.switch1_sure_btn:
                changeTextView(mSwitchEdit1, R.id.switch1_edit_btn, v.getId());
                break;

            case R.id.switch2_sure_btn:
                changeTextView(mSwitchEdit2, R.id.switch2_edit_btn, v.getId());
                break;

            case R.id.switch3_sure_btn:
                changeTextView(mSwitchEdit3, R.id.switch3_edit_btn, v.getId());
                break;
            case R.id.delete_btn:
                unBindDevice();
                break;
            default:
                break;
        }
    }

    private void changeEditView(EditText editText, int editTextId, int textViewId) {
        LinearLayout editBtn = (LinearLayout) findViewById(editTextId);
        TextView sureBtn = (TextView) findViewById(textViewId);
        editBtn.setVisibility(View.GONE);
        sureBtn.setVisibility(View.VISIBLE);
        editText.setEnabled(true);
        editText.requestFocus();
        if (editText.getText().length() > 0) {
            editText.setSelection(editText.getText().length());
        }
    }

    private void changeTextView(EditText editText, int editTextId, int textViewId) {
        LinearLayout editBtn = (LinearLayout) findViewById(editTextId);
        TextView sureBtn = (TextView) findViewById(textViewId);
        if (editText.getText().length() == 0 || StringUtils.isNull(editText.getText().toString())) {
            CommonUtils.showShortToast(getString(R.string.device_name_not_null));
            return;
        }
        editBtn.setVisibility(View.VISIBLE);
        sureBtn.setVisibility(View.GONE);
        editText.setEnabled(false);
    }

    private void saveAttributes() {
        if (!isEditing()) {
            finish();
            return;
        }
        if (mSwitchMainEdit.getText().length() == 0 || StringUtils.isNull(mSwitchMainEdit.getText().toString())) {
            CommonUtils.showShortToast(getString(R.string.setting_master_switch_name));
            return;
        }

        if (mSwitchEdit1.getText().length() == 0 || StringUtils.isNull(mSwitchEdit1.getText().toString())) {
            CommonUtils.showShortToast(getString(R.string.setting_socket_1_name));
            return;

        }
        if (mSwitchEdit2.getText().length() == 0 || StringUtils.isNull(mSwitchEdit2.getText().toString())) {
            CommonUtils.showShortToast(getString(R.string.setting_socket_2_name));
            return;

        }
        if (mSwitchEdit3.getText().length() == 0 || StringUtils.isNull(mSwitchEdit3.getText().toString())) {
            CommonUtils.showShortToast(getString(R.string.setting_socket_3_name));
            return;
        }
        String access_token = DataRepository.getInstance().getAccessToken();
        Gson gson = new Gson();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("switchMainName", mSwitchMainEdit.getText().toString());
        hashMap.put("switch1Name", mSwitchEdit1.getText().toString());
        hashMap.put("switch2Name", mSwitchEdit2.getText().toString());
        hashMap.put("switch3Name", mSwitchEdit3.getText().toString());
        String attributes = gson.toJson(hashMap);
        DialogUtils.showLoadingDialog(this);
        mPreserter.modifyDevice(access_token, attributes, mDeviceId);
    }

    /**
     * 解除绑定设备
     */
    private void unBindDevice() {
        final PhiGuideDialog unBindDialog = new PhiGuideDialog(this);
        unBindDialog.setTitle(getResources().getString(R.string.delete_device));
        unBindDialog.setMessage(getResources().getString(R.string.device_delete));
        unBindDialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.weight_line_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                unBindDialog.dismiss();
            }

        });
        unBindDialog.setRightGuideOnclickListener(getResources().getString(R.string.ok), R.color.syn_text_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                unBindDialog.dismiss();
                DialogUtils.showLoadingDialog(ModifyDeviceActivity.this);
                String access_token = DataRepository.getInstance().getAccessToken();
                mBindPresenter.unBindDevice(access_token, mDeviceId);
            }
        });
        unBindDialog.show();
    }

    /**
     * 取消保存
     */
    private void unSaveDeviceName() {
        if (!isEditing()) {
            finish();
            return;
        }
        final PhiGuideDialog unSaveDialog = new PhiGuideDialog(this);
        unSaveDialog.setTitle(getResources().getString(R.string.unsave_device_name_title));
        unSaveDialog.setMessage(getResources().getString(R.string.unsave_device_name_message));
        unSaveDialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.weight_line_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                unSaveDialog.dismiss();
            }

        });
        unSaveDialog.setRightGuideOnclickListener(getResources().getString(R.string.ok), R.color.syn_text_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                unSaveDialog.dismiss();
                finish();
            }
        });
        unSaveDialog.show();
    }

    //解绑
    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        DialogUtils.cancelLoadingDialog();
        if (t instanceof DeviceResultBean) {
            //解绑成功，跳转到设备列表界面
            CommonUtils.showToastBottom(R.string.unbind_device);
            EventBus.getDefault().post(new ModifyDeviceEvent());
            BaseApplication.getApplication().finishActivity("DeviceDetailsActivity");
            deleteLocalData(mDeviceId);
            recordUnbindEvent(mDeviceId);
            finish();
        }
    }


    /**
     * 记录设备解绑事件
     *
     * @param deviceId
     */
    private void recordUnbindEvent(String deviceId) {
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                UmengEventValueConfig.TYPE, deviceId + "_" + UmengEventValueConfig.UNBIND_DEVICE);
    }

    //修改插口名称
    @Override
    public void analysisModifyResponseBean(BaseResponseBean t) {
        DialogUtils.cancelLoadingDialog();
        if (t instanceof DeviceResultBean) {
            //修改成功，跳转到设备详情界面
            DeviceResultBean bean = (DeviceResultBean) t;
            CommonUtils.showToastBottom(R.string.modify_success);
            EventBus.getDefault().postSticky(new ModifyDeviceEvent(bean));
            updateLocalData(bean);
            recordEditSwitchNameEvent();
            finish();
        }
    }

    /**
     * 记录修改插口名字事件
     */
    private void recordEditSwitchNameEvent() {
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                UmengEventValueConfig.TYPE, mDeviceId + "_" + UmengEventValueConfig.EDIT_SWITCH_NAME);
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

    /**
     * 解除绑定，删除本地数据库
     *
     * @param deviceId
     */
    private void deleteLocalData(String deviceId) {
        DataRepository.getInstance().delete(deviceId);
    }

    /**
     * 修改设备插口名，更新本地数据库
     */
    private void updateLocalData(DeviceResultBean bean) {
        DataRepository.getInstance().updateDeviceSwitchName(bean);
    }

    /**
     * 获取设备固件版本号
     */
    private void getFirmwareVersion() {
        DialogUtils.showLoadingDialog(this);
        String access_token = DataRepository.getInstance().getAccessToken();
        mDevicePresenter.getFirmwareVersion(mDeviceId, access_token);
    }

    @Override
    public void analysisVersionResponseBean(BaseResponseBean t) {
        DialogUtils.cancelLoadingDialog();
        if (t.error.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            String firmwareVersion = bean.respData.result.firmware_version;
            if (!StringUtils.isNull(firmwareVersion)) {
                mFirmwareViesionTitle.setVisibility(View.VISIBLE);
                mFirmwareViesion.setVisibility(View.VISIBLE);
                mFirmwareViesion.setText(firmwareVersion);
            }
        }
    }

    @Override
    public void limitCallback() {
        CommonUtils.showShortToast(getString(R.string.max_device_plug_name));
    }

    private boolean isEditing() {
        if (mSwitchMainEdit.getText().toString().equals(mAttributesBean.getSwitchMainName())
                && mSwitchEdit1.getText().toString().equals(mAttributesBean.getSwitch1Name())
                && mSwitchEdit2.getText().toString().equals(mAttributesBean.getSwitch2Name())
                && mSwitchEdit3.getText().toString().equals(mAttributesBean.getSwitch3Name())) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        unSaveDeviceName();
    }

}
