package com.phicomm.smartplug.modules.loginregister.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
import com.phicomm.smartplug.modules.loginregister.forgetpassword.ForgetPasswordActivity;
import com.phicomm.smartplug.modules.loginregister.register.RegisterActivity;
import com.phicomm.smartplug.modules.mainpage.MainActivity;
import com.phicomm.smartplug.modules.token.TokenManager;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.MD5Utils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.phonenumber)
    EditText mPhoneNumberEdit;

    @BindView(R.id.password)
    EditText mPasswordEdit;

    private boolean isPasswordDisplay = true;
    @BindView(R.id.password_display_imageview)
    ImageView password_display_imageview;

    @BindView(R.id.rememberme_checkbox)
    CheckBox rememberme_checkbox;

    @BindView(R.id.bt_login)
    Button bt_login;

    private String phoneNo = "";
    private String password = "";
    private String mAuthorizationCode = "";

    private LoginContract.Presenter loginPresenter;

    public static final String PHONE_NUMBER = "phone_number";
    public static final int REQUEST_CODE_MODIFY_PASSWORD = 10;
    public static final int REQUEST_CODE_REGISTER = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_layout);

        initTitleView();

        initViews();

        setEditTextListener();

        //init presenter
        loginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(TAG, "onNewIntent");
        try {
            String phone = intent.getStringExtra(PHONE_NUMBER);
            mPhoneNumberEdit.setText(phone);
            if (!StringUtils.isNull(phone)) {
                mPasswordEdit.requestFocus();
                mPasswordEdit.setText("");
            }
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "resultCode = " + resultCode);
        LogUtils.d(TAG, "requestCode = " + requestCode);
        if ((resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODIFY_PASSWORD)) {
            try {
                String phone = data.getStringExtra(PHONE_NUMBER);
                mPhoneNumberEdit.setText(phone);
                if (!StringUtils.isNull(phone)) {
                    mPasswordEdit.requestFocus();
                    mPasswordEdit.setText("");
                }
            } catch (Exception ex) {

            }
        }
    }

    private void initTitleView() {
        TitlebarUtils.initTitleBar(this, R.string.login);
    }

    private void initViews() {
        password_display_imageview.setEnabled(true);

        boolean isRememberMe = DataRepository.getInstance().getRememberMe();
        rememberme_checkbox.setChecked(isRememberMe);

        try {
            if (this.getIntent() != null) {
                //back from modifypassword or forgetpassword
                String phone = this.getIntent().getStringExtra(PHONE_NUMBER);
                mPhoneNumberEdit.setText(phone);
                if (!StringUtils.isNull(phone)) {
                    mPasswordEdit.requestFocus();
                    mPasswordEdit.setText("");

                } else {
                    initRememberMe(isRememberMe);
                }
            } else {
                initRememberMe(isRememberMe);
            }
        } catch (Exception ex) {

        }
    }

    private void initRememberMe(boolean isRememberMe) {
        rememberme_checkbox.setChecked(isRememberMe);
        if (isRememberMe) {
            mPhoneNumberEdit.setText(DataRepository.getInstance().getUserName());
            mPasswordEdit.setText(DataRepository.getInstance().getPassword());
        } else {
            mPhoneNumberEdit.setText(null);
            mPasswordEdit.setText(null);
        }
    }


    private void setEditTextListener() {
        RxTextView.afterTextChangeEvents(mPhoneNumberEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                phoneNo = textViewAfterTextChangeEvent.editable().toString();
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(password)) {
                    bt_login.setEnabled(true);
                } else {
                    bt_login.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mPasswordEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                password = textViewAfterTextChangeEvent.editable().toString();
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(password)) {
                    bt_login.setEnabled(true);
                } else {
                    bt_login.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.password_display_imageview)
    public void clickDisplayPassword() {
        if (isPasswordDisplay) {
            isPasswordDisplay = false;
            password_display_imageview.setImageResource(R.drawable.icon_eye_open_white);
            //显示密码
            mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            isPasswordDisplay = true;
            password_display_imageview.setImageResource(R.drawable.icon_eye_close_white);
            //隐藏密码
            mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mPasswordEdit.setSelection(mPasswordEdit.getText().toString().length());
    }

    @OnClick(R.id.bt_login)
    public void login() {
        //斐讯账户登录
        phoneNo = mPhoneNumberEdit.getText().toString();
        password = mPasswordEdit.getText().toString();
        if (StringUtils.isNull(phoneNo)) {
            myActivity.showToast(R.string.phonenum_is_null);
            return;
        }

        if (!CommonUtils.checkMobile(phoneNo)) {
            myActivity.showToast(R.string.phonenum_is_illegal);
            return;
        }

        if (StringUtils.isNull(password)) {
            myActivity.showToast(R.string.password_is_null);
            return;
        }

        if (NetworkManagerUtils.instance().networkError()) {
            myActivity.showToast(R.string.net_connect_fail);
            return;
        }

        myActivity.showLoadingDialog(R.string.being_processed);
        password = MD5Utils.encryptedByMD5(password);
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();

        if (StringUtils.isNull(mAuthorizationCode)) {
            loginPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            loginPresenter.doPhoneLogin(mAuthorizationCode, phoneNo, password);
        }
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AuthorizationResponseBean) {
            AuthorizationResponseBean bean = (AuthorizationResponseBean) t;
            if (!StringUtils.isNull(bean.authorizationcode)) {
                mAuthorizationCode = bean.authorizationcode;
                DataRepository.getInstance().setAuthorizationCode(mAuthorizationCode);
                loginPresenter.doPhoneLogin(mAuthorizationCode, phoneNo, password);
            } else {
                myActivity.cancelLoadingDialog();
                myActivity.showToast(R.string.authorization_error);
            }
        } else if (t instanceof LoginResponseBean) {
            myActivity.cancelLoadingDialog();
            myActivity.showToast(R.string.login_success);

            LoginResponseBean bean = (LoginResponseBean) t;

            String uid = bean.uid;
            if (!StringUtils.isNull(uid)) {
                DataTrackAgent.commitAccount2Umeng(uid);
            }

            String access_token = bean.access_token;
            //保存斐讯云access_token
            DataRepository.getInstance().setAccessToken(access_token);
            DataRepository.getInstance().setCloudLoginStatus(true);
            TokenManager.getInstance().saveTokens(bean.access_token, bean.refresh_token,
                    bean.refresh_token_expire, bean.access_token_expire);

            //保存手机号
            DataRepository.getInstance().setUsername(phoneNo);

            //remember me
            if (rememberme_checkbox.isChecked()) {
                DataRepository.getInstance().setPassword(mPasswordEdit.getText().toString());
                DataRepository.getInstance().setRememberMe(true);
            } else {
                //clear remember me
                DataRepository.getInstance().setPassword("");
                DataRepository.getInstance().setRememberMe(false);
            }

            //跳转到主页
            startActivityClearTopAndFinishSelf(null, MainActivity.class);

            DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_LOGIN_LOGOUT, "type", "login");
        }
    }

    @OnClick(R.id.tv_register)
    public void clickRegister() {
        startActivityClearTop(null, RegisterActivity.class);
    }

    @OnClick(R.id.tv_forget_password)
    public void clickResetPassword() {
        Intent forgetPasswordIntent = new Intent(myActivity, ForgetPasswordActivity.class);
        forgetPasswordIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(forgetPasswordIntent, REQUEST_CODE_MODIFY_PASSWORD);
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
