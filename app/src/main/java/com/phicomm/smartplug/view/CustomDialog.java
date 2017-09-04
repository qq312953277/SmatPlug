package com.phicomm.smartplug.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseApplication;

public class CustomDialog extends Dialog {
    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public void setupDialog() {
        Window w = getWindow();
        if (w != null) {
            w.setGravity(Gravity.BOTTOM);
            w.setWindowAnimations(R.style.CustomDialogWindowStyle);

            WindowManager.LayoutParams lp = w.getAttributes();
            WindowManager wm = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display d = wm.getDefaultDisplay();
            lp.width = d.getWidth();
            w.setAttributes(lp);
        }
    }
}
