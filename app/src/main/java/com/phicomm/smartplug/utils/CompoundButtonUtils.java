package com.phicomm.smartplug.utils;

import android.widget.CompoundButton;

import com.phicomm.smartplug.R;

/**
 * Created by feilong.yang on 2017/8/3.
 * 用于逻辑结果返回错误时，回退CompoundButton的上一次状态
 */

public class CompoundButtonUtils {
    private static CompoundButtonUtils mInstace;
    private CompoundButton mButton;

    public static CompoundButtonUtils getInstace() {
        if (null == mInstace) {
            mInstace = new CompoundButtonUtils();
        }
        return mInstace;
    }

    /**
     * 记录当前操作的button
     *
     * @param button
     */
    public void recordOperation(CompoundButton button) {
        this.mButton = button;
    }


    /**
     * 还原上一次状态
     */
    public void resetButtonStatus(ResetButtonListener listener) {
        if (mButton != null) {
            mButton.setChecked(!mButton.isChecked());
            if (mButton.getId() == R.id.button1 && null != listener) {
                listener.callback();
            }
        }
    }

    public void removeButtonView() {
        if (null != mButton) {
            mButton = null;
        }
    }

    public interface ResetButtonListener {
        void callback();
    }
}
