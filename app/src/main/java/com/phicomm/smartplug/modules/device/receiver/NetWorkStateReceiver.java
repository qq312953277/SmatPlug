package com.phicomm.smartplug.modules.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phicomm.smartplug.event.NetWorkEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by feilong.yang on 2017/7/20.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new NetWorkEvent());
    }
}
