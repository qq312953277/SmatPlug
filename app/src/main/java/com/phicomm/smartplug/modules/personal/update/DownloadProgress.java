package com.phicomm.smartplug.modules.personal.update;

public class DownloadProgress {

    private static DownloadProgress mInstance;

    public static DownloadProgress getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadProgress();
        }
        return mInstance;
    }

    private boolean isDownloading = false;

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }
}
