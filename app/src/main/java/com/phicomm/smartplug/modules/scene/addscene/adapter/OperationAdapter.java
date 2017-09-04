package com.phicomm.smartplug.modules.scene.addscene.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.scene.model.OperationModel;

public class OperationAdapter extends BaseQuickAdapter<OperationModel, BaseViewHolder> {
    private Context mContext;
    private int currentSelected = 0;

    public OperationAdapter(Context context) {
        super(R.layout.item_scene_operation);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, OperationModel item) {
        // init view
        helper.setText(R.id.item_name, item.getOperationName())
                .setVisible(R.id.selected_indicator, helper.getLayoutPosition() == currentSelected);
    }

    public int getCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(int currentSelected) {
        this.currentSelected = currentSelected;
    }
}
