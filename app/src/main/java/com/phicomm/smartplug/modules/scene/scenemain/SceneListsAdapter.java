package com.phicomm.smartplug.modules.scene.scenemain;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;

public class SceneListsAdapter extends BaseQuickAdapter<SceneModel, BaseViewHolder> {
    private final String TAG = SceneListsAdapter.this.getClass().getSimpleName();

    private Context mContext;
    private ExecuteSceneInterface mSceneInterface;

    public SceneListsAdapter(Context context) {
        super(R.layout.item_scene);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, SceneModel item) {
        // init view
        helper.setText(R.id.scene_title, item.getSceneName())
                .setImageResource(R.id.trigger_icon, item.getTrigger().getImageId())
                .setText(R.id.trigger_type_label, item.getTrigger().getTriggerDescription())
                .setVisible(R.id.execute_button, item.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY)
                .setVisible(R.id.btn_enable_scene, item.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER);

        // set execute button
        helper.getView(R.id.execute_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SceneModel scene = getItem(helper.getLayoutPosition());
                if (mSceneInterface != null) {
                    mSceneInterface.executeScene(true, scene, TriggerModel.TRIGGER_MANUALLY);
                }
            }
        });

        // set enable button
        ToggleButton enableBtn = helper.getView(R.id.btn_enable_scene);
        enableBtn.setChecked(item.getStatus());
        enableBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SceneModel scene = getItem(helper.getLayoutPosition());
                if (mSceneInterface != null) {
                    mSceneInterface.executeScene(isChecked, scene, TriggerModel.TRIGGER_TIMER);
                }
            }
        });
    }

    public void setmSceneInterface(ExecuteSceneInterface mSceneInterface) {
        this.mSceneInterface = mSceneInterface;
    }
}