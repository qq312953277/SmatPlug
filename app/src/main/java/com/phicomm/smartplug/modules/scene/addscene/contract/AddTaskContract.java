package com.phicomm.smartplug.modules.scene.addscene.contract;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseView;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;

import java.util.List;

public class AddTaskContract {
    public interface View extends BaseView {
        void updateDeviceList(List<DeviceModel> list);
    }

    public interface Presenter extends BasePresenter {
        void getDeviceList();
    }
}
