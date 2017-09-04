package com.phicomm.smartplug.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.phicomm.smartplug.R;

/**
 * Created by feilong.yang on 2017/7/18.
 */

public class OfflineLoadingDialog extends Dialog {

    public OfflineLoadingDialog(Context context) {
        super(context, R.style.device_offline_loadingdialog);
    }

    public OfflineLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_offline_loading_layout);
        setCanceledOnTouchOutside(false);

    }

    public void showing() {
        if (!isShowing()) {
            show();
        }
    }

    public void hide() {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
