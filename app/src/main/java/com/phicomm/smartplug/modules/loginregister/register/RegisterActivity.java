package com.phicomm.smartplug.modules.loginregister.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
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
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.RegisterResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.VerifycodeResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.loginregister.login.LoginActivity;
import com.phicomm.smartplug.modules.mainpage.MainActivity;
import com.phicomm.smartplug.modules.token.TokenManager;
import com.phicomm.smartplug.modules.webh5.CommonWebActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.MD5Utils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    @BindView(R.id.phonenumber)
    EditText mPhoneNumberEdit;

    @BindView(R.id.code)
    EditText mCodeEdit;

    @BindView(R.id.captcha)
    EditText mCaptchaEdit;

    @BindView(R.id.captcha_code)
    ImageView mCaptchaImage;

    @BindView(R.id.btn_code)
    Button mGetCodeBtn;

    @BindView(R.id.password)
    EditText mPasswordEdit;

    private boolean isPasswordDisplay = true;
    @BindView(R.id.password_display_imageview)
    ImageView password_display_imageview;

    @BindView(R.id.password_strength)
    ImageView password_strength;

    @BindView(R.id.bt_register)
    Button mRegister;

    @BindView(R.id.protocal_checkbox)
    CheckBox mProtocalCheckbox;

    private boolean isProtocalChecked = false;
    private String phoneNo = "";
    private String verifyCode = "";
    private String captchaCode = "";
    private String captchaId = "";
    private String password = "";
    private String mAuthorizationCode = "";
    private boolean timerRunning = false;

    private static final int INPUT_TEXT_SIZE = 14;
    private static final int INPUT_PASSWORD_HINT_SIZE = 10;

    private RegisterContract.Presenter registerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

        initTitleView();

        initViews();

        //init register Presenter
        registerPresenter = new RegisterPresenter(this);

        setEditTextListener();

        //get image code first
        getCaptchaCode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTitleView() {
        TitlebarUtils.initTitleBar(this, R.string.new_user_register);
    }

    private void initViews() {
        password_display_imageview.setEnabled(true);

        //防抖
        RxView.clicks(mGetCodeBtn)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getVerifyCode();
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

    private void setEditTextListener() {
        RxTextView.afterTextChangeEvents(mPhoneNumberEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                phoneNo = textViewAfterTextChangeEvent.editable().toString();
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !timerRunning) {
                    mGetCodeBtn.setEnabled(true);
                } else {
                    mGetCodeBtn.setEnabled(false);
                }

                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !StringUtils.isNull(verifyCode)
                        && !StringUtils.isNull(password)
                        && isProtocalChecked) {
                    mRegister.setEnabled(true);
                } else {
                    mRegister.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mCaptchaEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                captchaCode = textViewAfterTextChangeEvent.editable().toString();
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !timerRunning) {
                    mGetCodeBtn.setEnabled(true);
                } else {
                    mGetCodeBtn.setEnabled(false);
                }

                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !StringUtils.isNull(verifyCode)
                        && !StringUtils.isNull(password)
                        && isProtocalChecked) {
                    mRegister.setEnabled(true);
                } else {
                    mRegister.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mCodeEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                verifyCode = textViewAfterTextChangeEvent.editable().toString();
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !StringUtils.isNull(verifyCode)
                        && !StringUtils.isNull(password)
                        && isProtocalChecked) {
                    mRegister.setEnabled(true);
                } else {
                    mRegister.setEnabled(false);
                }
            }
        });

        RxTextView.afterTextChangeEvents(mPasswordEdit).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                password = textViewAfterTextChangeEvent.editable().toString();

                if (!StringUtils.isNull(password)) {
                    mPasswordEdit.setTextSize(INPUT_TEXT_SIZE);
                } else {
                    mPasswordEdit.setTextSize(INPUT_PASSWORD_HINT_SIZE);
                }

                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !StringUtils.isNull(verifyCode)
                        && !StringUtils.isNull(password)
                        && isProtocalChecked) {
                    mRegister.setEnabled(true);
                } else {
                    mRegister.setEnabled(false);
                }

                checkPasswordStrength(password);
            }
        });
    }

    private void checkPasswordStrength(String password) {
        int strength = CommonUtils.getPasswordStrength(password);
        password_strength.setVisibility(View.VISIBLE);
        if (strength == 1) {
            password_strength.setImageResource(R.drawable.password_weak);
        } else if (strength == 2) {
            password_strength.setImageResource(R.drawable.password_middle);
        } else if (strength == 3) {
            password_strength.setImageResource(R.drawable.password_strong);
        } else {
            password_strength.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.captcha_code)
    public void getCaptchaCode() {
        if (NetworkManagerUtils.instance().networkError()) {
            return;
        }
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
        if (StringUtils.isNull(mAuthorizationCode)) {
            registerPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            mCaptchaImage.setImageResource(R.drawable.captcha_loading);
            registerPresenter.doGetCaptchaImageCode(mAuthorizationCode);
        }
    }

    /**
     * 获取验证码
     */
    public void getVerifyCode() {
        if (!CommonUtils.checkMobile(mPhoneNumberEdit.getText().toString())) {
            myActivity.showToast(R.string.phonenum_is_illegal);
            return;
        }

        captchaCode = mCaptchaEdit.getText().toString();
        if (StringUtils.isNull(captchaCode)) {
            myActivity.showToast(R.string.input_captcha_code);
            return;
        }

        if (NetworkManagerUtils.instance().networkError()) {
            return;
        }

        mGetCodeBtn.setEnabled(false);
        myActivity.showLoadingDialog(R.string.getting_msg_code);
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
        if (StringUtils.isNull(mAuthorizationCode)) {
            registerPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            registerPresenter.doCheckAccountRegistered(mAuthorizationCode, mPhoneNumberEdit.getText().toString());
        }
    }

    /**
     * 斐讯云注册
     */
    @OnClick(R.id.bt_register)
    public void register() {
        phoneNo = mPhoneNumberEdit.getText().toString();
        verifyCode = mCodeEdit.getText().toString();
        password = mPasswordEdit.getText().toString();

        captchaCode = mCaptchaEdit.getText().toString();

        if (StringUtils.isNull(phoneNo)) {
            myActivity.showToast(R.string.phonenum_is_null);
            return;
        }
        if (!CommonUtils.checkMobile(phoneNo)) {
            myActivity.showToast(R.string.phonenum_is_illegal);
            return;
        }

        if (StringUtils.isNull(verifyCode)) {
            myActivity.showToast(R.string.input_verify_code);
            return;
        }

        if (StringUtils.isNull(captchaCode)) {
            myActivity.showToast(R.string.input_captcha_code);
            return;
        }

        if (StringUtils.isNull(password)) {
            myActivity.showToast(R.string.password_is_null);
            return;
        }

        if (password.length() < 6) {
            CommonUtils.showToastBottom(getString(R.string.password_length_wrong));
            return;
        }

        if (!CommonUtils.isPassword(password)) {
            CommonUtils.showToastBottom(getString(R.string.password_format_wrong));
            return;
        }

        if (!mProtocalCheckbox.isChecked()) {
            myActivity.showToast(R.string.not_read_protocal);
            return;
        }

        if (NetworkManagerUtils.instance().networkError()) {
            return;
        }
        myActivity.showLoadingDialog(R.string.being_processed);
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
        password = MD5Utils.encryptedByMD5(password);
        if (StringUtils.isNull(mAuthorizationCode)) {
            registerPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            registerPresenter.doRegister(mAuthorizationCode, phoneNo, password, AppConstants.REQUEST_SOURCE, verifyCode);
        }
    }

    @OnCheckedChanged(R.id.protocal_checkbox)
    public void protocalChecked() {
        if (mProtocalCheckbox.isChecked()) {
            isProtocalChecked = true;
        } else {
            isProtocalChecked = false;
        }

        if (!StringUtils.isNull(phoneNo)
                && !StringUtils.isNull(captchaCode)
                && !StringUtils.isNull(verifyCode)
                && !StringUtils.isNull(password)
                && isProtocalChecked) {
            mRegister.setEnabled(true);
        } else {
            mRegister.setEnabled(false);
        }
    }


    @OnClick(R.id.protocal_textview)
    public void clickProtocal() {
        Intent intent = new Intent(myActivity, CommonWebActivity.class);
        intent.putExtra(CommonWebActivity.WEB_KEY_STATUS, getString(R.string.protocal_name));
        intent.putExtra(CommonWebActivity.WEB_VALUE_URL,
                AppConstants.APP_USER_REGISTER_PROTOCAL_BASE_URL
                        + CommonUtils.getSystemLanguage());
        startActivity(intent);
    }

    /**
     * 点击获取验证码按钮后，倒计时60s
     */
    @Override
    public void startVerfyCodeTimerTask() {
        final int timer = 60;
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> arg0) {
                arg0.onStart();
                int i = timer - 1;
                while (i >= 0) {
                    try {
                        Thread.sleep(1000);
                        arg0.onNext(i);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    i--;
                }
                arg0.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .compose(myActivity.<Integer>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        super.onStart();

                        int second = timer;
                        String showText = String.format("%s s", second);
                        mGetCodeBtn.setText(showText + getResources().getString(R.string.verifycode_resend));

                        mGetCodeBtn.setEnabled(false);
                        timerRunning = true;
                    }

                    @Override
                    public void onCompleted() {
                        mGetCodeBtn.setText(R.string.send_again);
                        mGetCodeBtn.setEnabled(true);
                        timerRunning = false;
                    }

                    @Override
                    public void onError(Throwable arg0) {
                        //do nothing
                    }

                    @Override
                    public void onNext(Integer arg0) {
                        int second = arg0;
                        String showText = String.format("%s s", second);
                        mGetCodeBtn.setText(showText + getResources().getString(R.string.verifycode_resend));
                    }
                });
    }

    private void enterIntoLoginDialog() {
        final PhiGuideDialog deleteDialog = new PhiGuideDialog(this);
        deleteDialog.setTitle(getResources().getString(R.string.login_tips));
        deleteDialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                deleteDialog.dismiss();

                //reenable getCodeBtn
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !timerRunning) {
                    mGetCodeBtn.setEnabled(true);
                } else {
                    mGetCodeBtn.setEnabled(false);
                }
            }

        });
        deleteDialog.setRightGuideOnclickListener(getResources().getString(R.string.please_login), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                deleteDialog.dismiss();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.PHONE_NUMBER, mPhoneNumberEdit.getText().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //reenable getCodeBtn
                if (!StringUtils.isNull(phoneNo)
                        && !StringUtils.isNull(captchaCode)
                        && !timerRunning) {
                    mGetCodeBtn.setEnabled(true);
                } else {
                    mGetCodeBtn.setEnabled(false);
                }
            }
        });
        deleteDialog.show();
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AuthorizationResponseBean) {
            AuthorizationResponseBean bean = (AuthorizationResponseBean) t;
            if (!StringUtils.isNull(bean.authorizationcode) && bean.error.equals(HttpErrorCode.success)) {
                mAuthorizationCode = bean.authorizationcode;
                DataRepository.getInstance().setAuthorizationCode(mAuthorizationCode);
                mCaptchaImage.setImageResource(R.drawable.captcha_loading);
                registerPresenter.doGetCaptchaImageCode(mAuthorizationCode);
            } else {
                myActivity.cancelLoadingDialog();
                myActivity.showToast(R.string.authorization_error);
            }
        } else if (t instanceof CaptchaResponseBean) {
            CaptchaResponseBean bean = (CaptchaResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                captchaId = bean.captchaid;
                Bitmap bitmap = CommonUtils.base64ToBitmap((String) bean.captcha);
                if (bitmap != null) {
                    mCaptchaImage.setImageBitmap(bitmap);
                } else {
                    mCaptchaImage.setImageResource(R.drawable.captcha_no_internet);
                }
            } else {
                //handler abnormal resonose
                int errorCode = Integer.parseInt(bean.error);
                int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);
                myActivity.showToast(errorStringRes);

                mCaptchaImage.setImageResource(R.drawable.captcha_no_internet);
            }
        } else if (t instanceof CheckphonenumberResponseBean) {
            CheckphonenumberResponseBean bean = (CheckphonenumberResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
                registerPresenter.doGetVerifyCode(mAuthorizationCode, mPhoneNumberEdit.getText().toString(), AppConstants.SMS_VERIFICATION,
                        captchaCode, captchaId);
            } else {
                myActivity.cancelLoadingDialog();
                int errorCode = Integer.parseInt(bean.error);

                if (errorCode == 14) {
                    enterIntoLoginDialog();
                } else {
                    int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);
                    myActivity.showToast(errorStringRes);
                }
            }
        } else if (t instanceof VerifycodeResponseBean) {
            myActivity.cancelLoadingDialog();
            VerifycodeResponseBean bean = (VerifycodeResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                myActivity.showToast(R.string.send_verifycode_success);
                startVerfyCodeTimerTask();
            } else {
                //handler captcha error
                int errorCode = Integer.parseInt(bean.error);
                int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);
                myActivity.showToast(errorStringRes);
                mGetCodeBtn.setEnabled(true);

                if (errorStringRes == R.string.get_verifycode_failed
                        || errorStringRes == R.string.captcha_error
                        || errorStringRes == R.string.captcha_expire
                        || errorStringRes == R.string.get_verifycode_too_fast
                        || errorStringRes == R.string.get_verifycode_count_expire
                        ) {
                    //refresh captcha code
                    getCaptchaCode();
                    mCaptchaEdit.setText("");
                }
            }
        } else if (t instanceof RegisterResponseBean) {
            RegisterResponseBean bean = (RegisterResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
                registerPresenter.doPhoneLogin(mAuthorizationCode, phoneNo, password);
            } else {
                myActivity.cancelLoadingDialog();
                int errorCode = Integer.parseInt(bean.error);

                if (errorCode == 14) {
                    enterIntoLoginDialog();
                } else {
                    int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);
                    myActivity.showToast(errorStringRes);
                }
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

            //donot remember me when loging after register
            DataRepository.getInstance().setPassword("");
            DataRepository.getInstance().setRememberMe(false);

            //跳转到主页
            startActivityClearTopAndFinishSelf(null, MainActivity.class);

            DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_LOGIN_LOGOUT, "type", "login");
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
