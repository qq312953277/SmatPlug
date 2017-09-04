package com.phicomm.smartplug.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phicomm.smartplug.R;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class PhiAvatarDialog extends Dialog {

    private TextView mCameraText;
    private TextView mAlbumText;

    public PhiAvatarDialog(Context context) {
        super(context);
        init(context);
    }

    public PhiAvatarDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected PhiAvatarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void init(Context mContext) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_avatar_choose, null);
        this.addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mCameraText = (TextView) layout.findViewById(R.id.camera);
        mAlbumText = (TextView) layout.findViewById(R.id.album);
    }

    public void setCameraClickListener(View.OnClickListener clickListener) {
        mCameraText.setOnClickListener(clickListener);
    }

    public void setAlbumClickListener(View.OnClickListener clickListener) {
        mAlbumText.setOnClickListener(clickListener);
    }
}
