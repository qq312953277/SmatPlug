package com.phicomm.smartplug.modules.loginregister.registerloginmain;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.AuthorizationResponseBean;
import com.phicomm.smartplug.modules.loginregister.login.LoginActivity;
import com.phicomm.smartplug.modules.loginregister.register.RegisterActivity;
import com.phicomm.smartplug.utils.StringUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yun.wang on 2017/7/10.
 */

public class RegisterLoginActivity extends BaseActivity implements RegisterLoginContract.View {

    @BindView(R.id.login_btn)
    Button loginBtn;

    @BindView(R.id.register_btn)
    Button registerBtn;

    @BindView(R.id.bg_imageview)
    ImageView bg_imageview;

    private RegisterLoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login_layout);

        mPresenter = new RegisterLoginPresenter(this);
        mPresenter.doAuthorization(AppConstants.CLIENT_ID, AppConstants.RESPONSE_TYPE, AppConstants.SCOPE, AppConstants.CLIENT_SECRET);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setStatusBar() {
        TitlebarUtils.noTitleBar(this);
    }

    @OnClick(R.id.login_btn)
    public void clickLoginBtn() {
        startActiityByExtra(null, LoginActivity.class);
    }

    @OnClick(R.id.register_btn)
    public void clickRegisterBtn() {
        startActiityByExtra(null, RegisterActivity.class);
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof AuthorizationResponseBean) {
            AuthorizationResponseBean bean = (AuthorizationResponseBean) t;
            if (!StringUtils.isNull(bean.authorizationcode)) {
                String mAuthorizationCode = bean.authorizationcode;
                DataRepository.getInstance().setAuthorizationCode(mAuthorizationCode);
            } else {
                myActivity.cancelLoadingDialog();
                myActivity.showToast(R.string.authorization_error);
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
