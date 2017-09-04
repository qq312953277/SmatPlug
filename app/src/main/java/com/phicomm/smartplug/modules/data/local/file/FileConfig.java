package com.phicomm.smartplug.modules.data.local.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public class FileConfig {
    private static final String BASE_LOCATION = Environment.getExternalStorageDirectory().getPath() + File.separator + "smartplug" + File.separator;
    public static final String IMAGE_LOCATION = BASE_LOCATION + "image" + File.separator;
    public static final String CLIP_IMAGE_LOCATION = BASE_LOCATION + "clip" + File.separator;
    public static final String CRASH_LOG_LOCATION = BASE_LOCATION + "crash" + File.separator;
    public static final String GLIDEMODE_IMAGE_CACHE_LOCATION = BASE_LOCATION + "imageglidecache" + File.separator;
}
