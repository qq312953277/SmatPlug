package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.event.DeviceListResultEvent;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.event.MultiLogoutEvent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.account.ModifypasswordResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.BindDeviceBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CaptchaResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.CheckphonenumberResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.LoginResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.RegisterResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.TokenUpdateBean;
import com.phicomm.smartplug.modules.data.remote.beans.loginregister.VerifycodeResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.DeleteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneDetailResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneListResponseBean;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public abstract class CustomSubscriber<T> extends Subscriber<T> {

    private final String TAG = "OkHttpLogInfo";

    abstract public void onCustomNext(T t);

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            LogUtils.d(TAG, "onError:" + e.toString());
        }
        DialogUtils.cancelLoadingDialog();

        //get errorstring by exception type
        int errorStringRes = R.string.common_error;
        if (e instanceof SocketTimeoutException) {
            errorStringRes = R.string.connect_timeout;
        } else if (e instanceof UnknownHostException || e instanceof SocketException) {
            errorStringRes = R.string.net_connect_fail;
        }

        CommonUtils.showToastBottom(BaseApplication.getContext().getString(errorStringRes));

        //统计网络异常类型
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(),
                DataTrackerConfig.EVENT_NETWORK_FAIL,
                "networkfailreason",
                e.getClass().getSimpleName());

        DataTrackAgent.reportError(BaseApplication.getContext(), e);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onNext(T t) {
        if (interceptAbnormalResponse(t)) {
            return;
        }
        onCustomNext(t);
    }

    public boolean interceptAbnormalResponse(T t) {
        if (t instanceof BaseResponseBean) {
            BaseResponseBean baseResponseBean = (BaseResponseBean) t;
            if (StringUtils.isNull(baseResponseBean.error) || baseResponseBean.error.equals("0")) {
                return false;//not intercept
            } else {
                try {

                    //token异常问题需要提前拦截
                    if (baseResponseBean.error.equals("5") //token失效
                            || baseResponseBean.error.equals("26")) { //账户已退出
                        //send logout event
                        DialogUtils.cancelLoadingDialog();
                        EventBus.getDefault().post(new LogoutEvent());
                        return true;
                    } else if (baseResponseBean.error.equals("30")) { //多端登录
                        //multi login,need logout
                        DialogUtils.cancelLoadingDialog();
                        EventBus.getDefault().post(new MultiLogoutEvent());
                        return true;
                    }

                    //not intercept list
                    if (t instanceof TokenUpdateBean
                            || t instanceof CaptchaResponseBean
                            || t instanceof VerifycodeResponseBean
                            || t instanceof CheckphonenumberResponseBean
                            || t instanceof RegisterResponseBean
                            || t instanceof BindDeviceBean
                            || t instanceof CancelSceneResponseBean
                            || t instanceof CreateSceneResponseBean
                            || t instanceof DeleteSceneResponseBean
                            || t instanceof EditSceneResponseBean
                            || t instanceof ExecuteSceneResponseBean
                            || t instanceof GetSceneDetailResponseBean
                            || t instanceof GetSceneListResponseBean
                            || t instanceof DeviceStatusBean) {
                        return false;//not intercept
                    }

                    DialogUtils.cancelLoadingDialog();

                    if (baseResponseBean.error.equals("113") || baseResponseBean.error.equals("100")) {
                        //no device binded，设备列表为空
                        EventBus.getDefault().post(new DeviceListResultEvent(baseResponseBean.error));
                        return true;
                    }

                    //handler abnormal resonose
                    int errorCode = Integer.parseInt(baseResponseBean.error);
                    int errorStringRes = HttpErrorCode.getErrorStringByErrorCode(errorCode);

                    if (errorStringRes == R.string.password_error && t instanceof ModifypasswordResponseBean) {
                        errorStringRes = R.string.old_password_error;
                    } else if (errorStringRes == R.string.password_error && t instanceof LoginResponseBean) {
                        errorStringRes = R.string.account_password_not_match;
                    }

                    CommonUtils.showToastBottom(BaseApplication.getContext().getString(errorStringRes));
                } catch (Exception ex) {

                }

                return true;
            }
        }
        return false;
    }
}
