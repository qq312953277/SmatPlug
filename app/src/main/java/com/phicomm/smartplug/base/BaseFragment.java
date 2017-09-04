package com.phicomm.smartplug.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phicomm.smartplug.event.DeviceListEvent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public class BaseFragment extends com.trello.rxlifecycle.components.support.RxFragment {

    public final String TAG = BaseFragment.this.getClass().getSimpleName();

    protected View myView = null;
    public BaseActivity myActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        myActivity = (BaseActivity) this.getActivity();

        //init eventbus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onVisibilityChangedToUser(isVisibleToUser);
        }
    }

    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            LogUtils.d("BaseFragment", "onVisibilityChangedToUser: onPageStart " + TAG);
            DataTrackAgent.onPageStart(TAG);
        } else {
            LogUtils.d("BaseFragment", "onVisibilityChangedToUser: onPageEnd " + TAG);
            DataTrackAgent.onPageEnd(TAG);
        }
    }

    public void showToast(String msg) {
        CommonUtils.showToastBottom(msg);
    }

    public void showToast(int resId) {
        CommonUtils.showToastBottom(myActivity.getResources().getString(resId));
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    /**
     * 新绑定设备刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDeviceList(DeviceListEvent event) {
        getDeviceList();
    }

    protected void getDeviceList() {

    }
}
