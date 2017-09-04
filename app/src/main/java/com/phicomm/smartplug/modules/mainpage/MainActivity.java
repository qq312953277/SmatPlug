package com.phicomm.smartplug.modules.mainpage;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.event.LogoutEvent;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.personal.update.CheckUpdateContract;
import com.phicomm.smartplug.modules.personal.update.CheckUpdatePresenter;
import com.phicomm.smartplug.modules.personal.update.UpdateVersionService;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.UriUtils;
import com.phicomm.smartplug.view.CustomViewPager;
import com.phicomm.widgets.alertdialog.PhiAlertDialog;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

public class MainActivity extends BaseActivity implements MyFragmentAdapter.GetFragmentCallback, CheckUpdateContract.View {
    @BindView(R.id.view_pager)
    CustomViewPager mViewPage;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    private MyFragmentAdapter pageAdapter;

    CheckUpdateContract.Presenter checkUpdatePresenter;
    private RxPermissions rxPermissions;

    private InstallUpdateReceiver mReceiver = new InstallUpdateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.phicomm.smartplug.R.layout.activity_main);
        initView();
        initData();
        checkNewVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.getApplication().firstEnterInMainActivity();

        // register install update receiver
        registerReceiver(mReceiver, new IntentFilter(UpdateVersionService.ACTION_INSTALL_UPDATE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister update receiver
        unregisterReceiver(mReceiver);
    }

    private void initView() {
        setupPager();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case com.phicomm.smartplug.R.id.device:
                        mViewPage.setCurrentItem(0, false);
                        break;
                    case com.phicomm.smartplug.R.id.scene:
                        mViewPage.setCurrentItem(1, false);
                        break;
                    case com.phicomm.smartplug.R.id.personal:
                        mViewPage.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setupPager() {
        pageAdapter = new MyFragmentAdapter(getSupportFragmentManager(), this);
        mViewPage.setOffscreenPageLimit(pageAdapter.getCount());
        mViewPage.setAdapter(pageAdapter);
        mViewPage.setNoScroll(true);
    }

    @Override
    public void initFragmentList(FragmentManager fm, List<Fragment> fragmentList) {
        for (MainFragmentTab tab : MainFragmentTab.values()) {
            try {
                BaseFragment fragment = null;
                List<Fragment> fs = fm.getFragments();
                if (fs != null) {
                    for (Fragment f : fs) {
                        if (f.getClass() == tab.clazz) {
                            fragment = (BaseFragment) f;
                            break;
                        }
                    }
                }
                if (fragment == null) {
                    fragment = tab.clazz.newInstance();
                }
                fragmentList.add(fragment);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        MainFragmentTab tab = MainFragmentTab.fromTabIndex(position);
        int resId = tab != null ? tab.resId : 0;
        return resId != 0 ? getText(resId) : "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("isFinishing:", "MainActivity:" + isFinishing());

        fixInputMethodManagerLeak(myActivity);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            outState.remove(FRAGMENTS_TAG);
        }
    }

    private void initData() {
        checkUpdatePresenter = new CheckUpdatePresenter(this);
        rxPermissions = new RxPermissions(this);
    }

    private void checkNewVersion() {
        if (NetworkManagerUtils.isNetworkAvailable(getContext())) {
            checkUpdatePresenter.checkNewVersion();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        DialogUtils.cancelLoadingDialog();
    }

    @Override
    public void updateProgress(int progress) {

    }

    @Override
    public void handleNewVersionInfo(final boolean isForceUpdate, String url, String versionName, String versionInfo) {
        View view = LayoutInflater.from(this).inflate(R.layout.download_apk_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.update_title);
        TextView message = (TextView) view.findViewById(R.id.update_message);
        title.setText(String.format(getString(R.string.version_update_title), versionName));
        message.setText(versionInfo);

        final PhiGuideDialog dialog = new PhiGuideDialog(this);
        dialog.setContentPanel(view);
        if (!isForceUpdate) {
            dialog.setLeftGuideOnclickListener(getResources().getString(R.string.update_later), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
                @Override
                public void onLeftGuideClick() {
                    dialog.dismiss();

                    // umeng
                    DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_UPDATE, "type", "update_later");
                }

            });
        }

        dialog.setRightGuideOnclickListener(getResources().getString(R.string.update_now), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                if (checkUpdatePresenter.isDownloading()) {
                    showToast(R.string.download_wait);
                    return;
                }

                if (!isForceUpdate) {
                    dialog.dismiss();
                }

                // request permission
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    checkUpdatePresenter.requestDownloadApk();

                                    // umeng
                                    DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_UPDATE, "type", "update_now");
                                } else {
                                    CommonUtils.showToastBottom(R.string.read_storage_permission_fail_tips);
                                }
                            }
                        });

            }
        });

        dialog.show();
        if (isForceUpdate) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void showNetWorkDialog() {
        PhiAlertDialog.Builder updateBuilder = new PhiAlertDialog.Builder(this);
        updateBuilder.setTitle(R.string.version_check)
                .setMessage(R.string.mobile_net_use)
                .setPositiveButton(R.string.download_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkUpdatePresenter.downloadApk();
                    }
                }).show();
    }

    @Override
    public void showNotification(int res) {

    }

    @Override
    public void onEventLogout(LogoutEvent msg) {
        super.onEventLogout(msg);

        //clear device data
        DataRepository.getInstance().deleteAll();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }


    private String getMIMEType(File file) {
        String name = file.getName();
        String var3 = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
    }

    private void installUpdate(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= 24) {
            Uri uriForFile = UriUtils.getUriFromFile(context, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
        } else {
            intent.setDataAndType(UriUtils.getUriFromFile(context, file), getMIMEType(file));
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.open_file_failure);
        }
    }

    class InstallUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(UpdateVersionService.ACTION_INSTALL_UPDATE)) {
                String path = intent.getStringExtra("path");
                File realFile = new File(path);
                installUpdate(realFile, MainActivity.this);
            }
        }
    }
}
