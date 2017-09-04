package com.phicomm.smartplug.modules.device.devicemain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.data.remote.beans.device.DeviceListsBean;
import com.phicomm.smartplug.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.phicomm.smartplug.view.SwitchButton;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<DeviceListsBean.DeviceBean> mDeviceList;
    private OnDeviceItemClickListener mOnDeviceItemClickListener;
    public static final int TYPE_ITEM = 0xff01;
    //用于区分是否可以刷新在线状态值了
    private boolean isRefreshed = false;

    public DeviceListAdapter(Context context) {
        this.mContext = context;
        mDeviceList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new DeviceItemViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_devicelist, parent, false));
            default:
                Log.d("error", "viewholder is null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DeviceItemViewHolder) {
            bindDeviceItemViewType((DeviceItemViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    private void bindDeviceItemViewType(DeviceItemViewHolder holder, final int position) {
        if (mDeviceList != null && mDeviceList.size() > 0) {
            final DeviceListsBean.DeviceBean deviceBean = mDeviceList.get(position);
            holder.imgDevice.setImageResource(R.drawable.device_default_icon);
            holder.tvDeviceName.setText(deviceBean.name);
            if (isRefreshed) {
                if (!StringUtils.isNull(deviceBean.getOnLineTitle())) {
                    holder.tvIsOnline.setText(deviceBean.getOnLineTitle());
                }
            }
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击item项进入每个设备详情页
                    mOnDeviceItemClickListener.onItemClick(position);

                }
            });
            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按item项进入每个设备详情页
                    mOnDeviceItemClickListener.onItemLongClick(0, "");
                    return false;
                }
            });
        }
    }

    class DeviceItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgDevice)
        ImageView imgDevice;
        @BindView(R.id.tvDeviceName)
        TextView tvDeviceName;
        @BindView(R.id.tvIsOnline)
        TextView tvIsOnline;
        @BindView(R.id.llParentLayout)
        RelativeLayout parentLayout;

        public DeviceItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setDeviceListData(List<DeviceListsBean.DeviceBean> deviceList) {
        if (this.mDeviceList == null) {
            this.mDeviceList = new ArrayList<>();
        }
        this.mDeviceList = deviceList;
        notifyDataSetChanged();
    }

    public void setListDataNotChanged(List<DeviceListsBean.DeviceBean> deviceList) {
        if (this.mDeviceList == null) {
            this.mDeviceList = new ArrayList<>();
        }
        this.mDeviceList = deviceList;
    }

    public void setRefrehed(boolean isrefreshed) {
        this.isRefreshed = isrefreshed;
    }

    public List<DeviceListsBean.DeviceBean> getDeviceListData() {
        return mDeviceList;
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener mOnItemClickLitener) {
        this.mOnDeviceItemClickListener = mOnItemClickLitener;

    }

    public interface OnDeviceItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position, String deviceId);
    }
}
