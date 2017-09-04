package com.phicomm.smartplug.modules.device.deviceconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.modules.personal.commonissues.CommonIssuesActivity;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import butterknife.BindView;

/**
 * Created by feilong.yang on 2017/8/10.
 */

public class ChooseDeviceTipsActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;
    @BindView(R.id.next_btn)
    Button mNextBtn;
    @BindView(R.id.tips_sure_btn)
    LinearLayout mTipsSureBtn;
    @BindView(R.id.check_btn)
    ToggleButton mCheckBtn;
    @BindView(R.id.image_gif)
    ImageView mGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device_tips_layout);
        initView();
    }

    private void initView() {
        TitlebarUtils.initTitleBar(this, R.string.add_wifi_plug);
        mTitleBar.addAction(new PhiTitleBar.ImageAction(R.drawable.help_tips_icon) {
            @Override
            public void performAction(View view) {
                startActivityClearTop(null, CommonIssuesActivity.class);
            }
        });

        Glide.with(this).load(R.drawable.choose_device_tips_bg).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mGifView);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityClearTop(null, ChooseWifiActivity.class);
                BaseApplication.getApplication().addActivity(TAG, ChooseDeviceTipsActivity.this);
            }
        });

        mTipsSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBtn.setChecked(!mCheckBtn.isChecked());
            }
        });

        mCheckBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mNextBtn.setEnabled(true);
                } else {
                    mNextBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getApplication().removeActivity(TAG);
    }
}
