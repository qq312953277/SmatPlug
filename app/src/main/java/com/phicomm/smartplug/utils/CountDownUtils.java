package com.phicomm.smartplug.utils;

import android.os.CountDownTimer;
import android.os.Handler;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: 倒计时工具
 * Version: 1.0.0
 */
public class CountDownUtils {

    public static final int START_TIMER = 666;
    public static final int STOP_TIMER = 999;

    /**
     * 倒计时控制器
     */
    private CountDownTimer mCountDownTimer;
    /**
     * 时间 秒
     */
    private static final long SECOND = 1000;

    /**
     * 额外的按钮恢复可点击需要的倒计次数,默认是60次
     */
    private int mExtraButtonRestoreTimes = 60;

    private Handler mHandler;

    public CountDownUtils(Handler handler) {
        this.mHandler = handler;
    }

    public void startTimerTask() {
        if (null == mCountDownTimer) {
            mCountDownTimer = new CountDownTimer(mExtraButtonRestoreTimes * SECOND, SECOND) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mHandler.sendMessage(mHandler.obtainMessage(START_TIMER, millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    mHandler.sendMessage(mHandler.obtainMessage(STOP_TIMER));
                }
            }.start();
        }
    }

    public void setRestorTime(int time) {
        mExtraButtonRestoreTimes = time;
    }

    public void stopTimer() {
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
