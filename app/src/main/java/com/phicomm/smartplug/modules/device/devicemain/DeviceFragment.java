package com.phicomm.smartplug.modules.device.devicemain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.base.BaseResponseBean;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.event.DeviceListResultEvent;
import com.phicomm.smartplug.event.ModifyDeviceEvent;
import com.phicomm.smartplug.event.NetWorkEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceAttributesBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceStatusBean;
import com.phicomm.smartplug.modules.device.deviceconnect.ChooseDeviceTipsActivity;
import com.phicomm.smartplug.modules.device.deviceconnect.UmengEventValueConfig;
import com.phicomm.smartplug.modules.device.devicedetails.DeviceDetailsActivity;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.refreshlayout.CustomPtrFrameLayoutRefreshHeader;
import com.phicomm.widgets.PhiTitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class DeviceFragment extends BaseFragment implements DeviceContract.View, DeviceListAdapter.OnDeviceItemClickListener {
    @BindView(R.id.device_list_recyclerview)
    RecyclerView mDeviceListRecyclerview;
    @BindView(R.id.frame_device)
    CustomPtrFrameLayoutRefreshHeader mFrameDevice;
    private DeviceContract.Presenter mDevicePresenter;
    private DeviceListAdapter mDeviceListAdapter;
    private List<DeviceListsBean.DeviceBean> mDeviceList;
    private static String TAG = "DeviceFragment";
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;
    @BindView(R.id.network_warning)
    LinearLayout mNetworkWarnning;
    @BindView(R.id.empty_layout)
    LinearLayout mEmptyLayout;
    //  @BindView(R.id.network_error_layout)
//  LinearLayout mNetworkErrorLayout;
    @BindView(R.id.reload_bt)
    Button mReloadBtn;
    private Timer mTimer;
    private boolean isFragmentPause = false;
    private boolean isVisibleToUser = false;
    private int statusResponseCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getDeviceLogic();
    }

    private void initView() {
        initTitleBar();
        mDeviceList = new ArrayList<>();
        mDeviceListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceListRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mDeviceListAdapter = new DeviceListAdapter(getActivity());
        mDeviceListRecyclerview.setAdapter(mDeviceListAdapter);
        mDevicePresenter = new DevicePresenter(this);
        setRefresh();
        setViewListener();
    }

    private void initNetworkStatus() {
        boolean isShowNetworkWarnView = showNetworkWarnView();
        if (!isShowNetworkWarnView) {
            getDeviceList();
        }
    }

    private void initTitleBar() {
        TitlebarUtils.initTitleBar(this.getActivity(), mTitleBar, R.string.device);
        //设置titlebar右侧图标
        mTitleBar.addAction(new PhiTitleBar.ImageAction(R.drawable.device_main_add_icon) {
            @Override
            public void performAction(View view) {
                Intent intent = new Intent(getActivity(), ChooseDeviceTipsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setViewListener() {
        mDeviceListAdapter.setOnDeviceItemClickListener(this);
        mReloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkManagerUtils.instance().isNetworkAvailable(getActivity())) {
                    DialogUtils.showLoadingDialog(getActivity());
                    getDeviceList();
                }
            }
        });
    }

    private void setRefresh() {
        mFrameDevice.setLastUpdateTimeRelateObject(this);
        mFrameDevice.setPtrHandler(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                //下拉刷新
                if (NetworkManagerUtils.instance().isNetworkAvailable(getActivity())) {
                    getDeviceList();
                }
                frame.refreshComplete();
            }
        });
    }

    private void getDeviceLogic() {
        boolean isShowNetworkWarnView = showNetworkWarnView();
        if (!isShowNetworkWarnView) {
            DialogUtils.showLoadingDialog(getContext());
            getDeviceList();
        } else {
            List<DeviceListsBean.DeviceBean> devs = getDeviceLocalList();
            mDeviceList.clear();
            if (null == devs || devs.size() == 0) {
                mEmptyLayout.setVisibility(View.VISIBLE);
            } else {
                mEmptyLayout.setVisibility(View.GONE);
                mDeviceList.addAll(devs);
                for (DeviceListsBean.DeviceBean bean : devs) {
                    if (bean.isOnline) {
                        bean.setOnLineTitle(getString(R.string.device_online));
                    } else {
                        bean.setOnLineTitle(getString(R.string.device_offline));
                    }
                }
                mDeviceListAdapter.setRefrehed(true);
                mDeviceListAdapter.setDeviceListData(devs);
            }
        }
    }

    /**
     * 获取设备列表请求
     */
    protected void getDeviceList() {
        String access_token = DataRepository.getInstance().getAccessToken();
        String productID = AppConstants.PRODUCT_ID;
        mDevicePresenter.getDeviceLists(access_token, productID);
        recordGetDeviceListEvent();
    }

    /**
     * 记录获取设备列表事件
     */
    private void recordGetDeviceListEvent() {
        DataTrackAgent.commitCountEvent2Umeng(BaseApplication.getContext(), DataTrackerConfig.EVENT_DEVICE,
                UmengEventValueConfig.TYPE, UmengEventValueConfig.GET_DEVICE_LIST);
    }

    /**
     * 获取每个设备的在线状态
     */
    private void getDeviceConnState(List<DeviceListsBean.DeviceBean> devices) {
        String access_token = DataRepository.getInstance().getAccessToken();
        if (null == mTimer) {
            mTimer = new Timer();
        } else {
            mTimer.cancel();
            mTimer = new Timer();
        }
        mTimer.schedule(new MyTimeTask(devices, access_token), 10, 20000);
    }

    class MyTimeTask extends TimerTask {
        private String access_token;
        private List<DeviceListsBean.DeviceBean> devices;

        public MyTimeTask(List<DeviceListsBean.DeviceBean> devices, String access_token) {
            this.access_token = access_token;
            this.devices = devices;
        }

        @Override
        public void run() {
            if (NetworkManagerUtils.isNetworkAvailable(getContext())) {
                for (int i = 0; i < devices.size(); i++) {
                    DeviceListsBean.DeviceBean bean = devices.get(i);
                    String deviceId = bean.deviceID;
                    mDevicePresenter.getDeviceConnState(access_token, deviceId, i);
                }
            }
        }
    }

    //获取列表回调
    @Override
    public void analysisResponseBean(BaseResponseBean bean) {
        if (bean instanceof DeviceListsBean) {
            DeviceListsBean listsBean = (DeviceListsBean) bean;
            mDeviceList.clear();
            if (null != listsBean && listsBean.devs != null && listsBean.devs.size() > 0) {
                mDeviceList.addAll(listsBean.devs);
                //将上一次设备状态，保存到新获取的设备list中
                List<DeviceListsBean.DeviceBean> oldDeviceList = mDeviceListAdapter.getDeviceListData();
                if (null != oldDeviceList && oldDeviceList.size() > 0) {
                    for (DeviceListsBean.DeviceBean oldBean : oldDeviceList) {
                        String deviceId = oldBean.getDeviceID();
                        boolean isOnline = oldBean.isOnline();
                        for (DeviceListsBean.DeviceBean newBean : listsBean.devs) {
                            if (newBean.getDeviceID().equals(deviceId)) {
                                newBean.setOnline(isOnline);
                                if (isOnline) {
                                    newBean.setOnLineTitle(getString(R.string.device_online));
                                } else {
                                    newBean.setOnLineTitle(getString(R.string.device_offline));
                                }
                            }
                        }
                    }
                }
                mDeviceListAdapter.setRefrehed(false);
                mDeviceListAdapter.setListDataNotChanged(listsBean.devs);
                mEmptyLayout.setVisibility(View.GONE);
//              mNetworkErrorLayout.setVisibility(View.GONE);
                //保存设备列表
                saveDeviceList(listsBean.devs);
                //执行获取设备状态请求
                getDeviceConnState(listsBean.devs);
            }
        }
    }


    /**
     * 获取设备列表为空
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDeviceList(DeviceListResultEvent event) {
        if (event.error.equals("113") || event.error.equals("100")) {
            //刷新在线状态
            mDeviceListAdapter.setDeviceListData(new ArrayList<DeviceListsBean.DeviceBean>());
            mEmptyLayout.setVisibility(View.VISIBLE);
//            mNetworkErrorLayout.setVisibility(View.GONE);
            DialogUtils.cancelLoadingDialog();
            if (null != mTimer) {
                mTimer.cancel();
            }
        }
    }

    @Override
    public void analysisError() {
//        mEmptyLayout.setVisibility(View.GONE);
//        mNetworkErrorLayout.setVisibility(View.VISIBLE);
    }

    //获取状态回调
    @Override
    public void analysisResponseBean(BaseResponseBean t, int position) {
        if (t.error.equals("0") && t instanceof DeviceStatusBean) {
            DeviceStatusBean bean = (DeviceStatusBean) t;
            boolean isOnline = bean.detail.onlineState;
            mDeviceListAdapter.getDeviceListData().get(position).setOnline(isOnline);
            if (isOnline) {
                mDeviceListAdapter.getDeviceListData().get(position).setOnLineTitle(getString(R.string.device_online));
            } else {
                mDeviceListAdapter.getDeviceListData().get(position).setOnLineTitle(getString(R.string.device_offline));
            }
            if (statusResponseCount < mDeviceList.size() - 1) {
                statusResponseCount += 1;
            } else {
                mDeviceListAdapter.setRefrehed(true);
                //刷新在线状态
                mDeviceListAdapter.notifyDataSetChanged();
                //更新设备状态
                statusResponseCount = 0;
                DialogUtils.cancelLoadingDialog();
            }
            updateDevice(mDeviceListAdapter.getDeviceListData().get(position));
        }
    }

    /**
     * 解绑，修改设备插口名称回调刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDeviceList(ModifyDeviceEvent event) {
        mDeviceList.clear();
        getDeviceList();
    }

    /**
     * 刷新fragment联网状态
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshNetWork(NetWorkEvent event) {
        if (!isFragmentPause) {
            initNetworkStatus();
        }
    }

    @Override
    public void onItemClick(int position) {
        DeviceListsBean.DeviceBean deviceBean = mDeviceListAdapter.getDeviceListData().get(position);
        String deviceId = deviceBean.deviceID;
        String deviceName = deviceBean.name;
        boolean isOnline = deviceBean.isOnline;
        DeviceAttributesBean attributesBean = deviceBean.attributes;
        Intent intent = new Intent();
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("attributes", attributesBean);
        intent.setClass(getActivity(), DeviceDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position, String deviceId) {


    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }


    /**
     * 保存设备列表
     */
    private void saveDeviceList(List<DeviceListsBean.DeviceBean> devs) {
        //先清空数据库
        DataRepository.getInstance().deleteAll();
        for (DeviceListsBean.DeviceBean deviceBean : devs) {
            DataRepository.getInstance().insert(deviceBean);
        }
    }


    /**
     * 更新设备
     *
     * @param deviceBean
     */
    private void updateDevice(DeviceListsBean.DeviceBean deviceBean) {
        DataRepository.getInstance().updateDevice(deviceBean);
    }

    /**
     * 获取本地设备
     *
     * @return
     */
    private ArrayList<DeviceListsBean.DeviceBean> getDeviceLocalList() {
        return DataRepository.getInstance().query();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentPause = true;
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isFragmentPause = true;
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentPause = false;
        if (null != mDeviceList && mDeviceList.size() > 0
                && isVisibleToUser) {
            getDeviceConnState(mDeviceList);
        }
        showNetworkWarnView();
    }

    private boolean showNetworkWarnView() {
        if (NetworkManagerUtils.instance().isNetworkAvailable(getActivity())) {
            mNetworkWarnning.setVisibility(View.GONE);
            return false;
        } else {
            mNetworkWarnning.setVisibility(View.VISIBLE);
            return true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (!isVisibleToUser && null != mTimer) {
            mTimer.cancel();
        } else {
            if (null != mDeviceList && mDeviceList.size() > 0) {
                getDeviceConnState(mDeviceList);
            }
        }
    }
}
