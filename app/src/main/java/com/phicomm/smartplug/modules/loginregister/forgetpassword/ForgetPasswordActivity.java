package com.phicomm.smartplug.modules.loginregister.forgetpassword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
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
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.ForgetpasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.VerifycodeResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.modules.loginregister.login.LoginActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.MD5Utils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
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

public class ForgetPasswordActivity extends BaseActivity implements ForgetPasswordContract.View {

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

    @BindView(R.id.bt_submit)
    Button mSubmitBtn;

    private String phoneNo = "";
    private String verifyCode = "";
    private String captchaCode = "";
    private String captchaId = "";
    private String password = "";
    private String mAuthorizationCode = "";
    private boolean timerRunning = false;

    private static final int INPUT_TEXT_SIZE = 14;
    private static final int INPUT_PASSWORD_HINT_SIZE = 10;

    private ForgetPasswordContract.Presenter resetPasswordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password_layout);

        initTitleView();

        initViews();

        resetPasswordPresenter = new ForgetPasswordPresenter(this);

        setEditTextListener();

        //get image code first
        getCaptchaCode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initTitleView() {
        TitlebarUtils.initTitleBar(this, R.string.reset_password);
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
                        && !StringUtils.isNull(password)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
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
                        && !StringUtils.isNull(password)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
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
                        && !StringUtils.isNull(password)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
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
                        && !StringUtils.isNull(password)) {
                    mSubmitBtn.setEnabled(true);
                } else {
                    mSubmitBtn.setEnabled(false);
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
            resetPasswordPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            mCaptchaImage.setImageResource(R.drawable.captcha_loading);
            resetPasswordPresenter.doGetCaptchaImageCode(mAuthorizationCode);
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
            resetPasswordPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            resetPasswordPresenter.doCheckAccountRegistered(mAuthorizationCode, mPhoneNumberEdit.getText().toString());
        }
    }

    @OnClick(R.id.bt_submit)
    public void submit() {
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

        if (NetworkManagerUtils.instance().networkError()) {
            return;
        }
        myActivity.showLoadingDialog(R.string.being_processed);
        mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
        password = MD5Utils.encryptedByMD5(password);
        if (StringUtils.isNull(mAuthorizationCode)) {
            resetPasswordPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
        } else {
            resetPasswordPresenter.doResetPassword(mAuthorizationCode, phoneNo, password, verifyCode);
        }
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

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AuthorizationResponseBean) {
            AuthorizationResponseBean bean = (AuthorizationResponseBean) t;
            if (!StringUtils.isNull(bean.authorizationcode) && bean.error.equals(HttpErrorCode.success)) {
                mAuthorizationCode = bean.authorizationcode;
                DataRepository.getInstance().setAuthorizationCode(mAuthorizationCode);
                mCaptchaImage.setImageResource(R.drawable.captcha_loading);
                resetPasswordPresenter.doGetCaptchaImageCode(mAuthorizationCode);
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
            if (bean.error.equals("14")) {//has been register
                mAuthorizationCode = DataRepository.getInstance().getAuthorizationCode();
                resetPasswordPresenter.doGetVerifyCode(DataRepository.getInstance().getAuthorizationCode(), mPhoneNumberEdit.getText().toString(), AppConstants.SMS_VERIFICATION,
                        captchaCode, captchaId);
            } else {
                myActivity.cancelLoadingDialog();
                int errorCode = Integer.parseInt(bean.error);
                int errorStringRes = R.string.account_not_exist;
                myActivity.showToast(errorStringRes);
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
        } else if (t instanceof ForgetpasswordResponseBean) {
            myActivity.cancelLoadingDialog();
            ForgetpasswordResponseBean bean = (ForgetpasswordResponseBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                myActivity.showToast(R.string.reset_success);
                Intent intent = myActivity.getIntent();
                intent.putExtra(LoginActivity.PHONE_NUMBER, mPhoneNumberEdit.getText().toString());
                myActivity.setResult(RESULT_OK, intent);
                myActivity.finish();
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
