package com.phicomm.smartplug.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.phicomm.smartplug.exception.CrashHandler;
import com.phicomm.smartplug.modules.data.DataRepository;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.device.model.DaoMaster;
import com.phicomm.smartplug.modules.device.model.DaoSession;
import com.phicomm.smartplug.modules.loginregister.login.LoginActivity;
import com.phicomm.smartplug.modules.loginregister.register.RegisterActivity;
import com.phicomm.smartplug.modules.loginregister.registerloginmain.RegisterLoginActivity;
import com.phicomm.smartplug.utils.CommonUtils;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */

public class BaseApplication extends Application {

    private static Context context;
    private static BaseApplication baseApplication = null;

    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;

    private static DaoSession daoSession;

    public static Context getContext() {
        return context;
    }

    public static BaseApplication getApplication() {
        if (baseApplication == null) {
            baseApplication = new BaseApplication();
        }
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

//        initCrashHandler();

        initMultiDex();

        // database
        initDatabase();

        //init data repository
        DataRepository.getInstance(getApplicationContext());

        //init umeng app
        DataTrackAgent.init(context);

        // leak canary
        if (CommonUtils.getAppChannel() != null && CommonUtils.getAppChannel().length() > 0 && CommonUtils.getAppChannel().equals("qa")) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
    }

    /**
     * init database
     */
    public void initDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "smartplug-db-encrypted" : "smartplug-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    /**
     * init MultiDex
     */
    public void initMultiDex() {
        MultiDex.install(this);
    }

    /**
     * init CrashHandler
     */
    public void initCrashHandler() {
        CrashHandler.getInstance().init(this);
    }

    //运用list来保存们每一个activity
    private List<Activity> mList = new LinkedList<Activity>();

    private HashMap<String, Activity> mHashMap = new HashMap<>();

    /**
     * add Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            mList.add(activity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * add Activity
     *
     * @param key
     * @param activity
     */
    public void addActivity(String key, Activity activity) {
        try {
            if (mHashMap.containsKey(key)) {
                mHashMap.remove(key);
            }
            mHashMap.put(key, activity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * remove Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        try {
            mList.remove(activity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * remove Activity
     */
    public void removeActivity(String key) {
        if (mHashMap.containsKey(key)) {
            mHashMap.remove(key);
        }
    }

    /**
     * 销毁指定Activity
     */
    public void finishActivity(String key) {
        if (mHashMap.containsKey(key)) {
            if (!mHashMap.get(key).isFinishing()) {
                mHashMap.get(key).finish();
            }
        }
    }

    public void firstEnterInMainActivity() {
        if (mList == null) {
            return;
        }
        for (Activity activity : mList) {
            if (activity instanceof RegisterLoginActivity) {
                activity.finish();
            }
            if (activity instanceof LoginActivity) {
                activity.finish();
            }
            if (activity instanceof RegisterActivity) {
                activity.finish();
            }
        }
    }

    /**
     * exit App and finish all activities
     */
    public void finishAllActivity() {
        //关闭list内的每一个activity
        if (mList == null) {
            return;
        }
        try {
            for (Activity activity : mList) {
                if (activity != null && !activity.isFinishing())
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishAllActivityAndExitApp() {
        finishAllActivity();

        android.os.Process.killProcess(android.os.Process.myPid());
        //System.exit(0);
    }
}
