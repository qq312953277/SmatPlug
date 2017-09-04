package com.phicomm.smartplug.modules.scene.addscene.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.scene.model.ControlModel;

public class ControlAdapter extends BaseQuickAdapter<ControlModel, BaseViewHolder> {
    private Context mContext;
    private int currentSelected = -1;

    public ControlAdapter(Context context) {
        super(R.layout.item_scene_control);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, ControlModel item) {
        Drawable right;

        // set control name and time setting
        helper.setText(R.id.item_name, item.getControlName());

        // set operation setting
        if (helper.getLayoutPosition() == currentSelected) {
            int hour = item.getDelayTime().getHour();
            int minute = item.getDelayTime().getMinute();

            // get time delay prompt string
            StringBuilder sb = new StringBuilder();
            sb.append(mContext.getResources().getString(R.string.delay_time_label));
            if (hour > 0) {
                sb.append(String.format(mContext.getResources().getString(R.string.n_hour), hour));
            }
            if (minute > 0) {
                sb.append(String.format(mContext.getResources().getString(R.string.n_minute), minute));
            }

            helper.setText(R.id.time, sb)
                    .setVisible(R.id.time, !(hour < 0 || minute < 0 || (hour == 0 && minute == 0)));

            right = mContext.getResources().getDrawable(R.drawable.selected_icon);
            helper.setText(R.id.operation, item.getCurrentOperation().getOperationName());
        } else {
            right = mContext.getResources().getDrawable(R.drawable.goto_detail_icon);
            helper.setVisible(R.id.time, false);
            helper.setText(R.id.operation, "");
        }
        right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
        ((TextView) helper.getView(R.id.operation)).setCompoundDrawables(null, null, right, null);
    }

    public int getCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(int currentSelected) {
        this.currentSelected = currentSelected;
    }
}
