package com.phicomm.smartplug.modules.device.devicedetails;

import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by feilong.yang on 2017/7/13.
 */

public class DeviceDetailPresenter implements DeviceDetailContract.Presenter {

    private DeviceDetailContract.View mBindView;

    private DeviceDetailContract.FirmwareView mFirmwareView;

    public DeviceDetailPresenter(DeviceDetailContract.View bindView) {
        mBindView = bindView;
    }

    public DeviceDetailPresenter(DeviceDetailContract.FirmwareView firmwareView) {
        this.mFirmwareView = firmwareView;
    }

    public JSONObject getParameter(int type, int status) {
        JSONObject json = new JSONObject();
        try {
            if (type == AppConstants.DEVICE_CONTROL) {
                json.put("action", "datapoint=");
                JSONObject params = new JSONObject();
                params.put("status", status);
                json.put("params", params);
            } else {
                json.put("action", "datapoint");
                JSONObject params = new JSONObject();
                json.put("params", params);
            }
            json.put("uuid", "00001");
            json.put("auth", "");
        } catch (Exception e) {
        }
        return json;
    }

    @Override
    public void getDeviceInfo(int type, int status, String deviceId, String access_token) {
        String strEntity = getParameter(type, status).toString();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        DataRepository.getInstance().getDeviceControl(mBindView.getRxLifeCycleObj(), access_token, new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mBindView.analysisInfoResponseBean(deviceStatusBean);
            }
        }, formBody, deviceId);
    }

    @Override
    public void setDeviceControl(int type, int status, String deviceId, String access_token) {
        String strEntity = getParameter(type, status).toString();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        DataRepository.getInstance().getDeviceControl(mBindView.getRxLifeCycleObj(), access_token, new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mBindView.analysisControlResponseBean(deviceStatusBean);
            }
        }, formBody, deviceId);
    }

    @Override
    public void getFirmwareVersion(String deviceId, String access_token) {
        String strEntity = getParameter().toString();
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        DataRepository.getInstance().getDeviceControl(mFirmwareView.getRxLifeCycleObj(), access_token, new CustomSubscriber<DeviceStatusBean>() {
            @Override
            public void onCustomNext(DeviceStatusBean deviceStatusBean) {
                mFirmwareView.analysisVersionResponseBean(deviceStatusBean);
            }
        }, formBody, deviceId);
    }

    public JSONObject getParameter() {
        JSONObject json = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            json.put("action", "firmwareVersion");
            json.put("uuid", "000001");
            json.put("auth", "");
            json.put("params", params);
        } catch (Exception e) {
        }
        return json;
    }

}
