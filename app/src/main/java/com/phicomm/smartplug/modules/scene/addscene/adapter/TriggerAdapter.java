package com.phicomm.smartplug.modules.scene.addscene.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseListAdapter;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;

public class TriggerAdapter extends BaseListAdapter<TriggerModel> {
    LayoutInflater inflater;
    private Context mContext;

    public TriggerAdapter(Context c) {
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gallery_item_layout, null);
        }

        ImageView im = (ImageView) convertView.findViewById(R.id.gallery_img_item);
        im.setImageResource(getModel(position).getImageId());

        TextView tv = (TextView) convertView.findViewById(R.id.gallery_item_label);
        tv.setText(getModel(position).getTriggerName());
        tv.setVisibility(View.VISIBLE);

        return convertView;
    }
}
