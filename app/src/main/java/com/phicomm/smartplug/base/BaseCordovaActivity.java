package com.phicomm.smartplug.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.WindowManager;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.event.MultiLogoutEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.loginregister.registerloginmain.RegisterLoginActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.alertdialog.PhiAlertDialog;

import org.apache.cordova.CordovaActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by yun.wang on 2017/7/7.
 */

public class BaseCordovaActivity extends CordovaActivity {

    public final String TAG = BaseCordovaActivity.this.getClass().getSimpleName();

    public BaseCordovaActivity myActivity;

    private static boolean isMultiLoginDialogShowing = false;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        //init butter knife
        ButterKnife.bind(this);

        //must after setcontentview
        setStatusBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //add to activity list
        BaseApplication.getApplication().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataTrackAgent.onPageStart(TAG);
        DataTrackAgent.onResume(this);

        //init eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataTrackAgent.onPageEnd(TAG);
        DataTrackAgent.onPause(this);

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BaseApplication.getApplication().removeActivity(this);
    }

    protected void setStatusBar() {
        TitlebarUtils.noTitleBar(this);
//        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    public void showToast(int stringRes) {
        CommonUtils.showToastBottom(this.getString(stringRes));
    }

    public void showToast(String string) {
        CommonUtils.showToastBottom(string);
    }

    public void showLoadingDialog(Integer stringRes) {
        if (stringRes == null) {
            DialogUtils.showLoadingDialog(this);
        } else {
            DialogUtils.showLoadingDialog(this, stringRes);
        }
    }

    public void cancelLoadingDialog() {
        DialogUtils.cancelLoadingDialog();
    }

    public void startActiityByExtra(Intent extras, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (extras != null) {
            intent.putExtras(extras);
        }
        this.startActivity(intent);
    }

    public void startActivityClearTop(Intent extras, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        this.startActivity(intent);
    }

    public void startActivityClearTopAndFinishSelf(Intent extras, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        this.startActivity(intent);
        this.finish();
    }

    //设置字体大小不随手机设置而改变
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * Logout EventBus
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogout(LogoutEvent msg) {

        //clear user data
        DataRepository.getInstance().setCloudLoginStatus(false);

        startActivityClearTopAndFinishSelf(null, RegisterLoginActivity.class);

        //clear all history activity
        BaseApplication.getApplication().finishAllActivity();

        DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_LOGIN_LOGOUT, "type", "logout");
    }

    /**
     * MutltiAccont EventBus
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMultiAccountLogout(MultiLogoutEvent msg) {

        //only show once
        if (isMultiLoginDialogShowing) {
            return;
        }
        isMultiLoginDialogShowing = true;
        PhiAlertDialog.Builder builder = new PhiAlertDialog.Builder(myActivity);
        builder.setTitle(R.string.exit);
        builder.setMessage(R.string.kick_tips);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new LogoutEvent());
                isMultiLoginDialogShowing = false;
            }
        });
        builder.show();
    }
}
