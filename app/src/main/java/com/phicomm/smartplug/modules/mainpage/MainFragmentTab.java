package com.phicomm.smartplug.modules.mainpage;

import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.modules.device.devicemain.DeviceFragment;
import com.phicomm.smartplug.modules.personal.personalmain.PersonalFragment;
import com.phicomm.smartplug.modules.scene.scenemain.SceneFragment;

public enum MainFragmentTab {
    DEVICE(0, DeviceFragment.class, com.phicomm.smartplug.R.string.device),
    SCENE(1, SceneFragment.class, com.phicomm.smartplug.R.string.scene),
    PERSONAL(3, PersonalFragment.class, com.phicomm.smartplug.R.string.personal);

    public final int tabIndex;
    public final Class<? extends BaseFragment> clazz;
    public final int resId;

    /**
     * @param index 选项卡
     * @param clazz Fragment 类
     * @param resId 选项名称id
     */
    MainFragmentTab(int index, Class<? extends BaseFragment> clazz, int resId) {
        this.tabIndex = index;
        this.clazz = clazz;
        this.resId = resId;
    }

    public static final MainFragmentTab fromTabIndex(int position) {
        for (MainFragmentTab tab : MainFragmentTab.values()) {
            if (tab.tabIndex == position) {
                return tab;
            }
        }
        return null;
    }
}
