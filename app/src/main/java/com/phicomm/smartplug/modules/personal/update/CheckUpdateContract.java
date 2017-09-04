package com.phicomm.smartplug.modules.personal.update;

import android.content.Context;

import com.phicomm.smartplug.base.BasePresenter;
import com.phicomm.smartplug.base.BaseView;

public class CheckUpdateContract {
    public interface View extends BaseView {
        void showNotification(int res);

        void showNetWorkDialog();

        void updateProgress(int progress);

        void dismissLoadingDialog();

        void handleNewVersionInfo(boolean isForceUpdate, String url, String versionName, String versionInfo);

        Context getContext();
    }

    public interface Presenter extends BasePresenter {
        void checkNewVersion();

        void requestDownloadApk();

        void downloadApk();

        boolean isDownloading();

        boolean hasNewVersion();
    }
}
