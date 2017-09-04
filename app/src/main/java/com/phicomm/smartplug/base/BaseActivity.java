package com.phicomm.smartplug.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.event.MultiLogoutEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.loginregister.registerloginmain.RegisterLoginActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.alertdialog.PhiAlertDialog;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public abstract class BaseActivity extends RxFragmentActivity {

    public final String TAG = BaseActivity.this.getClass().getSimpleName();

    public BaseActivity myActivity;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myActivity = this;

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
        //忽略统计的activity，因为已经统计了其名下的fragment
        if (!DataTrackerConfig.getActivityNameContainsViewPageFragment().contains(TAG)) {
            LogUtils.d("BaseActivity", "onResume " + TAG);
            DataTrackAgent.onPageStart(TAG);
            DataTrackAgent.onResume(this);
        }

        //init eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //忽略统计mainactivity，因为已经统计了其名下的fragment
        if (!DataTrackerConfig.getActivityNameContainsViewPageFragment().contains(TAG)) {
            LogUtils.d("BaseActivity", "onPause " + TAG);
            DataTrackAgent.onPageEnd(TAG);
            DataTrackAgent.onPause(this);
        }

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
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getApplication().removeActivity(this);
    }

    protected void setStatusBar() {
        TitlebarUtils.noTitleBar(this);
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
        if (!this.isFinishing()) {
            this.finish();
        }
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        try {
            // 通过反射机制，对 mCurRootView mServedView mNextServedView 进行置空...
            InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }

            String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
            Field f = null;
            Object obj_get = null;
            for (int i = 0; i < arr.length; i++) {
                String param = arr[i];
                try {
                    f = imm.getClass().getDeclaredField(param);
                    if (f.isAccessible() == false) {
                        f.setAccessible(true);
                    }
                    obj_get = f.get(imm);
                    if (obj_get != null && obj_get instanceof View) {
                        View v_get = (View) obj_get;
                        if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                            f.set(imm, null); // 置空，破坏掉path to gc节点
                        } else {
                            // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                            break;
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
