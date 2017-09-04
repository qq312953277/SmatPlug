package com.phicomm.smartplug.modules.personal.update;

import android.content.Intent;
import android.text.TextUtils;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseApplication;
import com.phicomm.smartplug.constants.AppConstants;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.remote.beans.update.CheckVersionResponseBean;
import com.phicomm.smartplug.modules.data.remote.http.CustomSubscriber;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;

public class CheckUpdatePresenter implements CheckUpdateContract.Presenter {
    private static final String TAG = "CheckUpdatePresenter";
    private CheckUpdateContract.View mView;
    private String mDownloadUrl;
    private String mDownloadApkName;
    private DataRepository mDataRepository = DataRepository.getInstance();

    public CheckUpdatePresenter(CheckUpdateContract.View view) {
        mView = view;
    }

    @Override
    public void checkNewVersion() {
        // set check info
        String appId = AppConstants.APP_ID;
        String channel = CommonUtils.getAppChannel();
        int verCode = CommonUtils.getVersionCode();

        // check new version
        DataRepository.getInstance().checkNewVersion(new CustomSubscriber<CheckVersionResponseBean>() {
            @Override
            public void onCustomNext(CheckVersionResponseBean checkVersionResponseBean) {
                int ret = checkVersionResponseBean.getRet();
                if (ret == 0) {
                    // set download info
                    mDownloadUrl = checkVersionResponseBean.getVerDown();
                    mDownloadApkName = AppConstants.APP_NAME + "-" + checkVersionResponseBean.getVerName();

                    // set new version flag as true
                    mDataRepository.setHasNewVersion(true);

                    // update ui
                    mView.handleNewVersionInfo(checkVersionResponseBean.getVerType() == 1, mDownloadUrl, checkVersionResponseBean.getVerName(), checkVersionResponseBean.getVerInfos());
                } else {
                    mView.showNotification(R.string.current_version_newest);

                    // set new version flag as false
                    mDataRepository.setHasNewVersion(false);
                }

                mView.dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
            }
        }, appId, channel, verCode);
    }

    @Override
    public void requestDownloadApk() {
        if (NetworkManagerUtils.isNetworkAvailable(BaseApplication.getContext())) {
            if (NetworkManagerUtils.isCurrWifiAvailable(BaseApplication.getContext())) {
                downloadApk();
            } else {
                mView.showNetWorkDialog();
            }
        } else {
            mView.showNotification(R.string.net_connect_fail);
        }
    }

    @Override
    public void downloadApk() {
        if (TextUtils.isEmpty(mDownloadUrl)) {
            return;
        }
        Intent service = new Intent(mView.getContext(), UpdateVersionService.class);
        service.putExtra(UpdateVersionService.DOWNLOAD_URL, mDownloadUrl);
        service.putExtra(UpdateVersionService.DOWNLOAD_NAME, mDownloadApkName + ".apk");
        mView.getContext().startService(service);
    }

    @Override
    public boolean isDownloading() {
        if (DownloadProgress.getInstance().isDownloading()) {
            mView.showNotification(R.string.download_wait);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNewVersion() {
        return mDataRepository.isHasNewVersion();
    }
}
