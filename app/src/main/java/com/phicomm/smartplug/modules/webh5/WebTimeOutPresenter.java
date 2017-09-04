package com.phicomm.smartplug.modules.webh5;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yun.wang on 2017/7/7.
 */

public class WebTimeOutPresenter implements WebTimeOutContract.Presenter {

    private WebTimeOutContract.View mView;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler;

    private static final int REQUEST_TIMEOUT_TIME = 10 * 1000;

    public WebTimeOutPresenter(WebTimeOutContract.View view, Handler handler) {
        this.mView = view;
        this.mHandler = handler;
    }

    @Override
    public void initTimer() {
        mTimer = new Timer();
    }

    @Override
    public void startTimerSchedule() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                //send message to show timeout layout
                Log.d("CommonWebActivity", "sendMessage=1");
                Message msg = new Message();
                msg.what = 1;
                if (mHandler != null) {
                    mHandler.sendMessage(msg);
                }

                clearTimer();
            }
        };
        if (mTimer == null) {
            return;
        }
        mTimer.schedule(mTimerTask, REQUEST_TIMEOUT_TIME, 1);

    }

    @Override
    public void destoryTimer() {
        clearTimer();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    private void clearTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }
}
