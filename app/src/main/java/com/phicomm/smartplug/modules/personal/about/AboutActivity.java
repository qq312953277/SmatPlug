package com.phicomm.smartplug.modules.personal.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TitlebarUtils.initTitleBar(this, R.string.about);

        View visitWebsite = findViewById(R.id.visit_website);
        View dialPhone = findViewById(R.id.dial_phone);
        visitWebsite.setOnClickListener(this);
        dialPhone.setOnClickListener(this);
        TextView version = (TextView) findViewById(R.id.about_version);
        String appName = getString(R.string.app_name_application);
        String versionStr = appName + "-" + CommonUtils.getVersionName();
        version.setText(versionStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visit_website:
                try {
                    String website = getString(R.string.phicomm_website);
                    Uri uri = Uri.parse("http://" + website);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                } finally {

                }
                break;
            case R.id.dial_phone:
                final PhiGuideDialog deleteDialog = new PhiGuideDialog(this);
                deleteDialog.setTitle(getResources().getString(R.string.phicomm_hotline));
                deleteDialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
                    @Override
                    public void onLeftGuideClick() {
                        deleteDialog.dismiss();
                    }

                });
                deleteDialog.setRightGuideOnclickListener(getResources().getString(R.string.dailnumber), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
                    @Override
                    public void onRightGuideClick() {
                        enterIntoDailPhone();
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();
                break;
        }
    }

    public void enterIntoDailPhone() {
        String hotLine = getString(R.string.phicomm_hotline);
        hotLine = hotLine.replace("-", "");
        Intent phone = new Intent(Intent.ACTION_DIAL);
        phone.setData(Uri.parse("tel:" + hotLine));
        startActivity(phone);
    }
}
