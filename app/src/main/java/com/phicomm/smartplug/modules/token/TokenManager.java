package com.phicomm.smartplug.modules.token;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.local.preference.PreferenceDef;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.TokenUpdateBean;
import com.phicomm.smartplug.modules.data.remote.http.HttpErrorCode;
import com.phicomm.smartplug.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import static com.phicomm.smartplug.modules.token.TokenManager.TokenStatus.REFRESH;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class TokenManager implements TokenManagerContract.View {

    private static TokenManager mTokenManager;

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;

    private static TokenManagerContract.Presenter mTokenmanagerPresenter;

    private TokenManager() {
        if (mTokenmanagerPresenter == null) {
            mTokenmanagerPresenter = new TokenManagerPresenter(this);
        }
    }

    public static TokenManager getInstance() {
        if (mTokenManager == null) {
            mTokenManager = new TokenManager();
        }
        return mTokenManager;
    }

    /**
     * 保存两个token以及它们的有效期限
     *
     * @param accessToken     访问令牌
     * @param refreshToken    刷新访问令牌的刷新令牌
     * @param accessValidity  访问令牌的有效期 单位秒
     * @param refreshValidity 刷新令牌的有效期 单位秒
     */
    public void saveTokens(String accessToken, String refreshToken, String refreshValidity, String accessValidity) {
        long currentTimeMillis = System.currentTimeMillis();

        DataRepository.getInstance().setAccessToken(accessToken);
        DataRepository.getInstance().setRefreshToken(refreshToken);
        DataRepository.getInstance().setAccessStartTime(currentTimeMillis);
        DataRepository.getInstance().setRefreshStartTime(currentTimeMillis);
        DataRepository.getInstance().setAccessValidity(Long.parseLong(accessValidity));
        DataRepository.getInstance().setRefreshValidity(Long.parseLong(refreshValidity));

    }

    /**
     * 保存刷新令牌
     *
     * @param accessToken    访问令牌
     * @param accessValidity 访问令牌有效期
     */
    private void saveAccessToken(String accessToken, String accessValidity) {
        long currentTimeMillis = System.currentTimeMillis();

        DataRepository.getInstance().setAccessToken(accessToken);
        DataRepository.getInstance().setAccessStartTime(currentTimeMillis);
        DataRepository.getInstance().setAccessValidity(Long.parseLong(accessValidity));
    }

    /**
     * 获取token的可用状态
     *
     * @return
     */
    public TokenStatus getTokenStatus() {
        String accessToken = DataRepository.getInstance().getAccessToken();
        String refreshToken = DataRepository.getInstance().getRefreshToken();
        long accessValidity = DataRepository.getInstance().getAccessValidity();
        long refreshValidity = DataRepository.getInstance().getRefreshValidity();
        long accessStartTime = DataRepository.getInstance().getAccessStartTime();
        long refreshStartTime = DataRepository.getInstance().getRefreshStartTime();
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(refreshToken)
                || accessValidity == 0 || refreshValidity == 0) {
            return TokenStatus.LOGOUT;
        }
        if (accessStartTime + accessValidity * 1000 > System.currentTimeMillis()) {
            return TokenStatus.ACCESS;
        }
        if (refreshStartTime + refreshValidity * 1000 > System.currentTimeMillis()) {
            return REFRESH;
        }
        return TokenStatus.LOGOUT;
    }

    /**
     * 获取访问令牌
     *
     * @return
     */
    public String getAccessToken() {
        return DataRepository.getInstance().getAccessToken();
    }

    public void clear() {
        DataRepository.getInstance().clearSPByName(PreferenceDef.SP_NAME_TOKEN);
    }

    /**
     * 刷新访问令牌
     *
     * @param authCode 授权码
     */
    public void refreshToken(@NonNull String authCode) {
        String refreshToken = DataRepository.getInstance().getRefreshToken();
        if (TextUtils.isEmpty(refreshToken) || TokenStatus.LOGOUT == getTokenStatus()) {
            return;
        }
        mTokenmanagerPresenter.refreshToken(refreshToken, authCode);
    }

    public enum TokenStatus {
        ACCESS,     //access token有效可用
        REFRESH,    //access token过期，可通过refresh token刷新得到
        LOGOUT     //refresh token也不可用了，需要清除登录状态重新登录
    }


    public static int checkAccessTokenAvailable() {
        TokenManager.TokenStatus tokenStatus = TokenManager.getInstance().getTokenStatus();

        //for test refresh token
//        tokenStatus = REFRESH;
        switch (tokenStatus) {
            case ACCESS:
                return LOGIN;
            case REFRESH:
                TokenManager.getInstance().refreshToken(DataRepository.getInstance().getAuthorizationCode());
                return LOGIN;
            case LOGOUT:
                DataRepository.getInstance().setCloudLoginStatus(false);
                return LOGOUT;
            default:
                return LOGIN;
        }
    }

    @Override
    public void analysisResponseBean(BaseResponseBean t) {
        if (t instanceof TokenUpdateBean) {
            TokenUpdateBean bean = (TokenUpdateBean) t;
            if (bean.error.equals(HttpErrorCode.success)) {
                //刷新成功
                if (!TextUtils.isEmpty(bean.access_token) && !TextUtils.isEmpty(bean.access_token_expire)) {
                    saveAccessToken(bean.access_token, bean.access_token_expire);
                    DataRepository.getInstance().setAccessToken(bean.access_token);
                }
            } else if (bean.error.equals("5") || bean.error.equals("6") || bean.error.equals("30")) {

                //handler abnormal resonose
                int errorCode = Integer.parseInt(bean.error);
                int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);
                CommonUtils.showToastBottom(BaseApplication.getContext().getString(errorStringRes));

                //send logout event
                EventBus.getDefault().post(new LogoutEvent());
            } else {
                //refresh fail,maybe network error,refresh again

            }
        }
    }
}
