package com.phicomm.smartplug.modules.personal.update;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseService;

import java.io.File;

public class UpdateVersionService extends BaseService {
    private String TAG = "UpdateService";
    private DownloadManager mManager;
    private DownloadCompleteReceiver mReceiver;
    private String mUrl;
    private String mApkName;

    public static final String DOWNLOAD_URL = "download_url";
    public static final String DOWNLOAD_NAME = "download_name";
    private static final String DirType = "SmartPlug";

    public static final String ACTION_INSTALL_UPDATE = "install_update";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUrl = intent.getStringExtra(DOWNLOAD_URL);
        mApkName = intent.getStringExtra(DOWNLOAD_NAME);

        String path = Environment.getExternalStoragePublicDirectory(DirType) + File.separator + mApkName;
        Log.d(TAG, "update service start");

        // check if file exist
        File file = new File(path);
        if (file.exists()) {
            //deleteFileWithPath(path);
            if (Uri.fromFile(file) != null) {
                String realPath = getRealFilePath(getApplicationContext(), Uri.fromFile(file));
                File realFile = new File(realPath);
                if (realFile.exists()) {
                    sendInstallUpdateRequest(realFile, getApplicationContext());
                }
            }
            UpdateVersionService.this.stopSelf();
            return Service.START_NOT_STICKY;
        }

        // file not exist, download it
        try {
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent0);
            } catch (Exception ex) {
                showToast(R.string.download_failure);
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initDownManager() {
        // init downloader
        mManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        // init download request
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(mUrl));

        // setup used network
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        // disallow over roaming
        down.setAllowedOverRoaming(false);

        // init mime type
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(mUrl));
        down.setMimeType(mimeString);

        // set notification visible in status bar
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // visible in download
        down.setVisibleInDownloadsUi(true);

        // set download location
        down.setDestinationInExternalPublicDir(DirType, mApkName);

        // start download
        long reference = mManager.enqueue(down);

        // set downloading flag
        DownloadProgress.getInstance().setDownloading(true);
        showToast(R.string.download_wait);

        // register receiver
        mReceiver = new DownloadCompleteReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                showToast(R.string.update_success);
                DownloadProgress.getInstance().setDownloading(false);
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mManager.getUriForDownloadedFile(downId) != null) {
                    preSendInstallUpdateRequest(context, getRealFilePath(context, mManager.getUriForDownloadedFile(downId)));
                } else {
                    showToast(R.string.download_failure);
                }
                UpdateVersionService.this.stopSelf();
            }
        }

        private void preSendInstallUpdateRequest(Context context, String path) {
            File file = new File(path);
            if (file.exists()) {
                sendInstallUpdateRequest(file, context);
            } else {
                showToast(R.string.download_failure);
            }
        }
    }

    public String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public void sendInstallUpdateRequest(File file, Context context) {
        Intent intent = new Intent();
        intent.putExtra("path", file.getAbsolutePath());
        intent.setAction(ACTION_INSTALL_UPDATE);

        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.open_file_failure);
        }
    }
}