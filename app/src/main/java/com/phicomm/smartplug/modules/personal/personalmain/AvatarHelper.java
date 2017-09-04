package com.phicomm.smartplug.modules.personal.personalmain;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.modules.data.local.file.FileConfig;
import com.phicomm.smartplug.modules.personal.personalnfo.RoundClipImageActivity;
import com.phicomm.smartplug.utils.BitmapUtils;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.FileUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.view.PhiAvatarDialog;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.lang.ref.WeakReference;

import rx.functions.Action1;

/**
 * Created by yun.wang
 * Date :2017/6/30
 * Description: ***
 * Version: 1.0.0
 */
public class AvatarHelper {
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragment;
    private String[] mItems;
    private OnItemSelectListener mListener;

    private RxPermissions rxPermissions;

    public static String getDefaultAvatarPath(Context context) {
        String path = FileUtils.checkDirPath(FileConfig.CLIP_IMAGE_LOCATION) + "default_avatar.png";
        if (new File(path).exists()) {
            return path;
        }
        Drawable drawable = context.getResources().getDrawable(R.drawable.default_avatar);
        Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable);
        BitmapUtils.savePhotoToSDCard(bitmap, path);
        return path;
    }

    public static void startActivityForCaptureImage(Activity activity, int requestCode, Uri uri) {
        if (activity == null || uri == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("Avatar", "camera not found");
            CommonUtils.showToastBottom(BaseApplication.getContext().getResources().getString(R.string.no_camera));
        }
    }

    public static void startActivityForPickImage(Activity activity, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("Avatar", "album not found");
            CommonUtils.showToastBottom(BaseApplication.getContext().getResources().getString(R.string.no_album));
        }
    }

    public static void startActivityForClipImage(Activity activity, int requestCode, Uri uri) {
        if (activity == null || uri == null) {
            return;
        }
        //custom clip
        Intent intent = new Intent(activity, RoundClipImageActivity.class);
        intent.setData(uri);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForCaptureImage(Fragment fragment, int requestCode, Uri uri) {
        if (fragment == null || uri == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("Avatar", "activity not found");
            CommonUtils.showToastBottom(BaseApplication.getContext().getResources().getString(R.string.no_camera));

        }
    }

    public static void startActivityForPickImage(Fragment fragment, int requestCode) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            LogUtils.e("Avatar", "album not found");
            CommonUtils.showToastBottom(BaseApplication.getContext().getResources().getString(R.string.no_album));
        }
    }

    public static void startActivityForClipImage(Fragment fragment, int requestCode, Uri uri) {
        if (fragment == null || uri == null) {
            return;
        }
        Intent intent = new Intent(fragment.getActivity(), RoundClipImageActivity.class);
        intent.setData(uri);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        fragment.startActivityForResult(intent, requestCode);

    }

    public AvatarHelper(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
        mItems = new String[2];
        mItems[0] = activity.getString(R.string.take_photo);
        mItems[1] = activity.getString(R.string.album);
        rxPermissions = new RxPermissions(activity);
    }

    public AvatarHelper(Fragment fragment) {
        Activity activity = fragment.getActivity();
        mActivity = new WeakReference<Activity>(activity);
        mFragment = new WeakReference<Fragment>(fragment);
        mItems = new String[2];
        mItems[0] = activity.getString(R.string.take_photo);
        mItems[1] = activity.getString(R.string.album);
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    public void showImageSelectorDialog() {
        if (mActivity.get() == null) {
            return;
        }
        final Activity context = mActivity.get();

        final PhiAvatarDialog dialog = new PhiAvatarDialog(context, R.style.simpleAlertDialog);
        dialog.setCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    rxPermissions
                                            .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    if (aBoolean) {
                                                        mListener.onCaptureImageItemSelected();
                                                    } else {
                                                        CommonUtils.showToastBottom(R.string.read_storage_permission_fail_tips);
                                                    }
                                                }
                                            });

                                } else {
                                    CommonUtils.showToastBottom(R.string.camera_permission_fail_tips);
                                }
                            }
                        });
            }
        });
        dialog.setAlbumClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    mListener.onPickImageItemSelected();
                                } else {
                                    CommonUtils.showToastBottom(R.string.read_storage_permission_fail_tips);
                                }
                            }
                        });
            }
        });
        dialog.show();
    }

    public interface OnItemSelectListener {
        void onCaptureImageItemSelected();

        void onPickImageItemSelected();
    }
}
