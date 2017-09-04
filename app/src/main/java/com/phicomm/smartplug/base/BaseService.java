package com.phicomm.smartplug.base;

import android.app.Service;

import com.phicomm.smartplug.utils.CommonUtils;

/**
 * Created by yun.wang on 2017/7/6.
 */

public abstract class BaseService extends Service {

    public BaseService myService;

    @Override
    public void onCreate() {
        super.onCreate();
        myService = this;
    }

    public void showToast(int stringRes) {
        CommonUtils.showToastBottom(this.getString(stringRes));
    }

    public void showToast(String string) {
        CommonUtils.showToastBottom(string);
    }
}
