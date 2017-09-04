package com.phicomm.smartplug.modules.scene.addscene.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.scene.model.ControlModel;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;

public class TaskAdapter extends BaseQuickAdapter<TaskModel, BaseViewHolder> {
    private Context mContext;

    public TaskAdapter(Context context) {
        super(R.layout.item_scene_task);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskModel item) {
        DeviceModel device = item.getDevice();
        ControlModel control = device.getCurrentControl();
        int hour = control.getDelayTime().getHour();
        int minute = control.getDelayTime().getMinute();

        // get time delay prompt string
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getResources().getString(R.string.delay_time_label));
        if (hour > 0) {
            sb.append(String.format(mContext.getResources().getString(R.string.n_hour), hour));
        }
        if (minute > 0) {
            sb.append(String.format(mContext.getResources().getString(R.string.n_minute), minute));
        }

        // init view
        helper.setImageResource(R.id.device_icon, device.getImageId())
                .setText(R.id.device_name, device.getDeviceName())
                .setText(R.id.control_name, control.getControlName())
                .setText(R.id.operation, control.getCurrentOperation().getOperationName())
                .setText(R.id.time_delay, sb)
                .setVisible(R.id.time_delay, !(hour < 0 || minute < 0 || (hour == 0 && minute == 0)));

        // set delete actionModel
        helper.getView(R.id.delete_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(helper.getLayoutPosition());
                notifyDataSetChanged();
            }
        });
    }
}
