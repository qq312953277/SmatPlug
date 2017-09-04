package com.phicomm.smartplug.utils;

import android.content.Context;
import android.os.Handler;

import com.phicomm.smartplug.view.OfflineLoadingDialog;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class OfflineLoadingUtil {

    private static OfflineLoadingDialog dialog;

    public static void showLoadingDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new OfflineLoadingDialog(context);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelLoadingDialog();
            }
        }, 1000);
        dialog.showing();

    }

    public static void cancelLoadingDialog() {
        if (dialog != null) {
            dialog.hide();
            dialog = null;
        }
    }
}
