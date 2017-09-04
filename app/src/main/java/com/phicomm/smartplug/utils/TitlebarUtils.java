package com.phicomm.smartplug.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.phicomm.smartplug.R;
import com.phicomm.widgets.PhiTitleBar;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class TitlebarUtils {
    //设置titleBar中间文字
    public static void initTitleBar(final Activity activity, PhiTitleBar titleBar, int strResid) {
        if (null != titleBar) {
            titleBar.setTitle(strResid);
            titleBar.setTitleColor(Color.WHITE);
            titleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));

            setActivityImmersive(activity, titleBar);
        }
    }

    public static void initTitleBar(final Activity activity, PhiTitleBar titleBar, String str) {
        if (null != titleBar) {
            titleBar.setTitle(str);
            titleBar.setTitleColor(Color.WHITE);
            titleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));

            setActivityImmersive(activity, titleBar);
        }
    }

    public static void initTitleBar(final Activity activity, PhiTitleBar titleBar) {
        if (null != titleBar) {
            titleBar.setTitleColor(Color.WHITE);
            titleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));

            setActivityImmersive(activity, titleBar);
        }
    }

    public static void initTitleBar(final Activity activity) {
        PhiTitleBar titleBar = (PhiTitleBar) activity.findViewById(R.id.title_bar);
        if (titleBar != null) {
            CharSequence title = activity.getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleBar.setTitle(title);
            }
            titleBar.setTitleColor(Color.WHITE);
            titleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));
            titleBar.setLeftImageResource(R.drawable.button_back);
            titleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            setActivityImmersive(activity, titleBar);
        }
    }

    //设置左侧带返回按钮的titleBar中间文字
    public static void initTitleBar(final Activity activity, int strResid) {
        PhiTitleBar phiTitleBar = (PhiTitleBar) activity.findViewById(R.id.title_bar);
        if (null != phiTitleBar) {
            phiTitleBar.setTitle(strResid);
            phiTitleBar.setTitleColor(Color.WHITE);
            phiTitleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));
            phiTitleBar.setLeftImageResource(R.drawable.button_back);
            phiTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
            setActivityImmersive(activity, phiTitleBar);
        }
    }

    //自定义一个带返回键回调的方法
    public static void initTitleBar(final Activity activity, int strResid, View.OnClickListener listener) {
        PhiTitleBar phiTitleBar = (PhiTitleBar) activity.findViewById(R.id.title_bar);
        if (null != phiTitleBar) {
            phiTitleBar.setTitle(strResid);
            phiTitleBar.setTitleColor(Color.WHITE);
            phiTitleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));
            phiTitleBar.setLeftImageResource(R.drawable.button_back);
            phiTitleBar.setLeftClickListener(listener);
            setActivityImmersive(activity, phiTitleBar);
        }
    }

    public static void initTitleBar(final Activity activity, String str) {
        PhiTitleBar phiTitleBar = (PhiTitleBar) activity.findViewById(R.id.title_bar);
        if (null != phiTitleBar) {
            phiTitleBar.setTitle(str);
            phiTitleBar.setTitleColor(Color.WHITE);
            phiTitleBar.setTitleSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.title_text_size));
            phiTitleBar.setLeftImageResource(R.drawable.button_back);
            phiTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
            setActivityImmersive(activity, phiTitleBar);
        }
    }

    private static void setActivityImmersive(Activity activity, PhiTitleBar titleBar) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            titleBar.setImmersive(true);
        } else {
            titleBar.setImmersive(false);
        }
        titleBar.setAllCaps(false);
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void noTitleBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.setImmersive(true);
        } else {
            activity.setImmersive(false);
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
