package com.phicomm.smartplug.modules.scene.utils;

import com.phicomm.smartplug.R;

public class SceneResultCode {
    public static int getResultString(int resultCode) {
        switch (resultCode) {
            case -4:
                return R.string.scene_never_started;
            case -3:
                return R.string.blank;
            case -2:
                return R.string.cancel_execute;
            case -1:
                return R.string.wait_for_execute;
            case 0:
                return R.string.already_execute;
            case 103:
                return R.string.not_persistent_connection;
            case 100:
            case 111:
            case 112:
            case 113:
                return R.string.device_not_bind;
            default:
                return R.string.execute_fail;
        }
    }
}
