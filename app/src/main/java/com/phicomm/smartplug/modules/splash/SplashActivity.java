package com.phicomm.smartplug.modules.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.loginregister.registerloginmain.RegisterLoginActivity;
import com.phicomm.smartplug.modules.mainpage.MainActivity;
import com.phicomm.smartplug.modules.token.TokenManager;
import com.phicomm.widgets.alertdialog.PhiAlertDialog;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class SplashActivity extends BaseActivity {

    private static final long DELAY_Millis = 1500;

    private RxPermissions rxPermissions;

    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        requestAllPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
        fixInputMethodManagerLeak(myActivity);
    }

    @Override
    protected void setStatusBar() {
        //set full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void requestAllPermissions() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            delayStartIntentAndFinish();
                        } else {
                            showPermissionAlert();
                        }
                    }
                });
    }

    private void showPermissionAlert() {
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + BaseApplication.getContext().getPackageName());
                Uri uri = Uri.fromParts("package", BaseApplication.getContext().getPackageName(), null);
                intent.setData(uri);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        };
        new PhiAlertDialog.Builder(this)
                .setMessage(R.string.please_grant_app_permission)
                .setPositiveButton(R.string.ok, okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    private void checkLoginStatus() {
        //是否引导过
        if (DataRepository.getInstance().isAppGuided()) {
            //如果后期有引导页，可以再这里加上引导页的跳转
            return;
        }
        //是否登录过
        if (DataRepository.getInstance().isCloudLogined()) {
            if (checkAccessToken()) {
                //直接跳转到首页
                myActivity.startActivityClearTopAndFinishSelf(null, MainActivity.class);

                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_LOGIN_LOGOUT, "type", "login");
            } else {
                //如果还没有登录过，跳转到登录界面
                myActivity.startActivityClearTopAndFinishSelf(null, RegisterLoginActivity.class);
            }
        } else {
            //如果还没有登录过，跳转到登录界面
            myActivity.startActivityClearTopAndFinishSelf(null, RegisterLoginActivity.class);
        }
        //实现淡入浅出的效果
        overridePendingTransition(R.anim.alpha_anim_in, R.anim.alpha_anim_out);
    }

    public boolean checkAccessToken() {
        boolean mLoginOK = false;
        switch (TokenManager.checkAccessTokenAvailable()) {
            case TokenManager.LOGIN:
                mLoginOK = true;
                break;
            case TokenManager.LOGOUT:
                mLoginOK = false;
                break;
            default:
                mLoginOK = false;
        }
        return mLoginOK;
    }

    private void delayStartIntentAndFinish() {
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, DELAY_Millis);
    }
}
