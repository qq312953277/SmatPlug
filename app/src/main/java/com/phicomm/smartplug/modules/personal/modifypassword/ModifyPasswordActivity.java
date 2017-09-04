package com.phicomm.smartplug.modules.personal.modifypassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.loginregister.login.LoginActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.MD5Utils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class ModifyPasswordActivity extends BaseActivity implements ModifyPasswordContract.View {

    @BindView(R.id.password)
    EditText mPasswordEdit;

    private boolean isPasswordDisplay = true;
    @BindView(R.id.password_display_imageview)
    ImageView password_display_imageview;

    @BindView(R.id.new_password_1)
    EditText mNewPasswordEdit1;

    private boolean isNewPasswordDisplay1 = true;
    @BindView(R.id.new_password_display_imageview_1)
    ImageView new_password_display_imageview_1;

    @BindView(R.id.new_password_2)
    EditText mNewPasswordEdit2;

    private boolean isNewPasswordDisplay2 = true;
    @BindView(R.id.new_password_display_imageview_2)
    ImageView new_password_display_imageview_2;

    @BindView(R.id.bt_submit)
    Button mSubmitBtn;

    private String mOldPassword = "";
    private String mNewPassword1 = "";
    private String mNewPassword2 = "";
    private String mAuthorizationCode = "";

    private static final int INPUT_TEXT_SIZE = 14;
    private static final int INPUT_PASSWORD_HINT_SIZE = 12;

    private ModifyPasswordContract.Presenter modifyPasswordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_password_layout);

        initTitleView();

        initViews();

        setEditTextListener();

        modifyPasswordPresenter = new ModifyPasswordPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTitleView() {
        PhiTitleBar phiTitleBar = (PhiTitleBar) this.findViewById(R.id.title_bar);
        TitlebarUtils.initTitleBar(this, R.string.modify_password);
    }

    private void initViews() {
        password_display_imageview.setEnabled(true);
        new_password_display_imageview_1.setEnabled(true);
        new_password_display_imageview_2.setEnabled(true);
    }

    private void setEditTextListener() {
        RxTextView.afterTextChangeEvents(mPasswordEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                mOldPassword = textViewAfterTextChangeEvent.editable().toString();

                if (!StringUtils.isNull(mOldPassword)
                        && !StringUtils.isNull(mNewPassword1)
                        && !StringUtils.isNull(mNewPassword2)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mNewPasswordEdit1).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                mNewPassword1 = textViewAfterTextChangeEvent.editable().toString();

                if (!StringUtils.isNull(mNewPassword1)) {
                    mNewPasswordEdit1.setTextSize(INPUT_TEXT_SIZE);
                } else {
                    mNewPasswordEdit1.setTextSize(INPUT_PASSWORD_HINT_SIZE);
                }

                if (!StringUtils.isNull(mOldPassword)
                        && !StringUtils.isNull(mNewPassword1)
                        && !StringUtils.isNull(mNewPassword2)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mNewPasswordEdit2).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                mNewPassword2 = textViewAfterTextChangeEvent.editable().toString();

                if (!StringUtils.isNull(mOldPassword)
                        && !StringUtils.isNull(mNewPassword1)
                        && !StringUtils.isNull(mNewPassword2)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.password_display_imageview)
    public void clickDisplayPassword() {
        if (isPasswordDisplay) {
            isPasswordDisplay = false;
            password_display_imageview.setImageResource(R.drawable.icon_eye_open);
            //显示密码
            mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            isPasswordDisplay = true;
            password_display_imageview.setImageResource(R.drawable.icon_eye_close);
            //隐藏密码
            mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mPasswordEdit.setSelection(mPasswordEdit.getText().toString().length());
    }

    @OnClick(R.id.new_password_display_imageview_1)
    public void clickDisplayNewPassword1() {
        if (isNewPasswordDisplay1) {
            isNewPasswordDisplay1 = false;
            new_password_display_imageview_1.setImageResource(R.drawable.icon_eye_open);
            //显示密码
            mNewPasswordEdit1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            isNewPasswordDisplay1 = true;
            new_password_display_imageview_1.setImageResource(R.drawable.icon_eye_close);
            //隐藏密码
            mNewPasswordEdit1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mNewPasswordEdit1.setSelection(mNewPasswordEdit1.getText().toString().length());
    }

    @OnClick(R.id.new_password_display_imageview_2)
    public void clickDisplayNewPassword2() {
        if (isNewPasswordDisplay2) {
            isNewPasswordDisplay2 = false;
            new_password_display_imageview_2.setImageResource(R.drawable.icon_eye_open);
            //显示密码
            mNewPasswordEdit2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            isNewPasswordDisplay2 = true;
            new_password_display_imageview_2.setImageResource(R.drawable.icon_eye_close);
            //隐藏密码
            mNewPasswordEdit2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mNewPasswordEdit2.setSelection(mNewPasswordEdit2.getText().toString().length());
    }


    @OnClick(R.id.bt_submit)
    public void submit() {
        mOldPassword = mPasswordEdit.getText().toString();
        mNewPassword1 = mNewPasswordEdit1.getText().toString();
        mNewPassword2 = mNewPasswordEdit2.getText().toString();

        if (StringUtils.isNull(mOldPassword)) {
            myActivity.showToast(R.string.old_password_is_null);
            return;
        }

        if (StringUtils.isNull(mNewPassword1)) {
            myActivity.showToast(R.string.new_password1_is_null);
            return;
        }

        if (StringUtils.isNull(mNewPassword2)) {
            myActivity.showToast(R.string.new_password2_is_null);
            return;
        }

        if (!mNewPassword1.equals(mNewPassword2)) {
            myActivity.showToast(R.string.new_password_is_not_compare);
            return;
        }

        if (mOldPassword.equals(mNewPassword1)) {
            myActivity.showToast(R.string.password_same);
            return;
        }

        if (mNewPassword1.length() < 6) {
            CommonUtils.showToastBottom(getString(R.string.newpassword_length_wrong));
            return;
        }

        if (!CommonUtils.isPassword(mNewPassword1)) {
            CommonUtils.showToastBottom(getString(R.string.newpassword_format_wrong));
            return;
        }

        if (NetworkManagerUtils.instance().networkError()) {
            return;
        }
        myActivity.showLoadingDialog(R.string.being_processed);
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
        mOldPassword = MD5Utils.encryptedByMD5(mOldPassword);
        mNewPassword1 = MD5Utils.encryptedByMD5(mNewPassword1);

        if (StringUtils.isNull(mAuthorizationCode)) {
            modifyPasswordPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            modifyPasswordPresenter.doModifyPassword(mOldPassword, mNewPassword1);
        }
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AuthorizationResponseBean) {
            AuthorizationResponseBean bean = (AuthorizationResponseBean) t;
            if (!StringUtils.isNull(bean.authorizationcode) && bean.error.equals(HttpErrorCode.success)) {
                mAuthorizationCode = bean.authorizationcode;
                DataRepository.getInstance().setAuthorizationCode(mAuthorizationCode);


                modifyPasswordPresenter.doModifyPassword(mOldPassword, mNewPassword1);
            }
        } else if (t instanceof ModifypasswordResponseBean) {
            ModifypasswordResponseBean bean = (ModifypasswordResponseBean) t;
            myActivity.cancelLoadingDialog();
            if (bean.error.equals(HttpErrorCode.success)) {
                myActivity.showToast(R.string.reset_success);

                //need relogin for new password
                //send logout event
//                EventBus.getDefault().post(new LogoutEvent());

                //set phonenumber
                Intent intent = new Intent();
                intent.putExtra(LoginActivity.PHONE_NUMBER, DataRepository.getInstance().getUserName());

                //clear user data
                DataRepository.getInstance().setCloudLoginStatus(false);

                startActivityClearTopAndFinishSelf(intent, LoginActivity.class);

                //clear all history activity
                BaseApplication.getApplication().finishAllActivity();
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}