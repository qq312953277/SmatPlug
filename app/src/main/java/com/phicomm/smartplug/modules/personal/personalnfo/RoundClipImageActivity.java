package com.phicomm.smartplug.modules.personal.personalnfo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.utils.BitmapUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.utils.UriUtils;
import com.phicomm.widgets.PhiTitleBar;
import com.phicomm.widgets.clip.PhiClipImageView;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by wenhui02.liu on 2016/12/12.
 */

public class RoundClipImageActivity extends BaseActivity {

    @BindView(R.id.id_clipImageLayout)
    PhiClipImageView mClipImageLayout;

    private Uri mUri;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clip_round_image);

        initTitleView();

        initData();
    }

    private void initTitleView() {
        PhiTitleBar phiTitleBar = (PhiTitleBar) this.findViewById(R.id.title_bar);
        TitlebarUtils.initTitleBar(this, "裁剪");
        phiTitleBar.setActionTextColor(R.color.white);
        phiTitleBar.addAction(new PhiTitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = mClipImageLayout.clip();
                        Intent intent = new Intent();
                        bitmap = BitmapUtils.zoomBitmap(bitmap, 300, 300);//zoom the bitmap for bundle size limit
                        intent.putExtra("data", bitmap);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).start();
            }
        });
    }

    private void initData() {
        //get data
        mUri = getIntent().getData();

        rxPermissions = new RxPermissions(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        doClicpImage();
                    }
                });
    }

    private void doClicpImage() {
        Bitmap bitmap;
        int scaleBitmapWidth = BitmapUtils.getScreenWidth(this);
        if (Build.VERSION.SDK_INT >= 24) {
            bitmap = UriUtils.getBitmapFromUri(this, mUri);
            bitmap = BitmapUtils.zoomBitmap(bitmap, scaleBitmapWidth, scaleBitmapWidth);
        } else {
            String path = UriUtils.getRealFilePathFromUri(getApplicationContext(), mUri);
            bitmap = BitmapUtils.convertToBitmap(path, scaleBitmapWidth, scaleBitmapWidth);
        }

        if (bitmap == null) {
            return;
        }
        mClipImageLayout.setClipImage(bitmap);
    }
}
