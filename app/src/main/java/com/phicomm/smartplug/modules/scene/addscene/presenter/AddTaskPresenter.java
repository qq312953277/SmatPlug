package com.phicomm.smartplug.modules.scene.addscene.presenter;

import android.content.Context;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceAttributesBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.scene.addscene.contract.AddTaskContract;
import com.phicomm.smartplug.modules.scene.model.ControlModel;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;
import com.phicomm.smartplug.modules.scene.model.OperationModel;

import java.util.ArrayList;
import java.util.List;

public class AddTaskPresenter implements AddTaskContract.Presenter {
    private AddTaskContract.View mView;
    private Context mContext;

    public AddTaskPresenter(Context context, AddTaskContract.View view) {
        this.mView = view;
        mContext = context;
    }

    @Override
    public void getDeviceList() {
        // token
        String access_token = DataRepository.getInstance().getAccessToken();

        // product id
        String productID = AppConstants.PRODUCT_ID;

        // get device list
        DataRepository.getInstance().getBindDevs(mView.getRxLifeCycleObj(), new com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber<DeviceListsBean>() {
            @Override
            public void onCustomNext(DeviceListsBean deviceListBean) {
                List<DeviceModel> devices = new ArrayList<>();

                // parse devices
                for (DeviceListsBean.DeviceBean dev : deviceListBean.devs) {
                    DeviceModel device = new DeviceModel();
                    device.setImageId(R.drawable.device_default_icon);
                    device.setDeviceId(dev.deviceID);
                    device.setDeviceName(dev.name);
                    device.setControls(initControls(dev.attributes));
                    devices.add(device);
                }
                mView.updateDeviceList(devices);
            }
        }, access_token, productID);
    }

    private List<ControlModel> initControls(DeviceAttributesBean nameBean) {
        List<ControlModel> out = new ArrayList<>();

        // get main control
        ControlModel control = new ControlModel();
        if (nameBean.getSwitchMainName() != null) {
            control.setControlName(nameBean.getSwitchMainName());
        } else {
            control.setControlName(mContext.getResources().getString(R.string.master_switch));
        }
        control.setControlId(0);
        control.setOperations(initOperation());
        out.add(control);

        // get sub control 1
        control = new ControlModel();
        if (nameBean.getSwitch1Name() != null) {
            control.setControlName(nameBean.getSwitch1Name());
        } else {
            control.setControlName(mContext.getResources().getString(R.string.socket_1));
        }
        control.setControlId(1);
        control.setOperations(initOperation());
        out.add(control);

        // get sub control 2
        control = new ControlModel();
        if (nameBean.getSwitch2Name() != null) {
            control.setControlName(nameBean.getSwitch2Name());
        } else {
            control.setControlName(mContext.getResources().getString(R.string.socket_2));
        }
        control.setControlId(2);
        control.setOperations(initOperation());
        out.add(control);

        // get sub control 3
        control = new ControlModel();
        if (nameBean.getSwitch3Name() != null) {
            control.setControlName(nameBean.getSwitch3Name());
        } else {
            control.setControlName(mContext.getResources().getString(R.string.socket_3));
        }
        control.setControlId(3);
        control.setOperations(initOperation());
        out.add(control);

        return out;
    }

    private List<OperationModel> initOperation() {
        List<OperationModel> out = new ArrayList<>();
        OperationModel operation = new OperationModel();
        operation.setOperation(OperationModel.OPEN);
        out.add(operation);

        operation = new OperationModel();
        operation.setOperation(OperationModel.CLOSE);
        out.add(operation);
        return out;
    }
}
