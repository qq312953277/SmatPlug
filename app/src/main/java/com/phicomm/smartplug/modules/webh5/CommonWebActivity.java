package com.phicomm.smartplug.modules.webh5;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseCordovaActivity;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebChromeClient;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yun.wang on 2017/7/7.
 */

public class CommonWebActivity extends BaseCordovaActivity implements WebTimeOutContract.View {

    public static final String WEB_KEY_STATUS = "web_key";
    public static final String WEB_VALUE_URL = "web_url";

    @BindView(R.id.webprogressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.web_ll)
    View mWebViewLayout;

    @BindView(R.id.cordovaWebView)
    SystemWebView mSystemWebView;

    @BindView(R.id.no_network_ll)
    View mNoNetWorkLayout;

    @BindView(R.id.tv_no_network)
    TextView mNonetWorkTextView;

    private String web_key = null;
    private String mWeb_url = null;
    private SystemWebViewEngine mSystemWebViewEngine;
    private WebTimeOutContract.Presenter mPresenter;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);

        LogUtils.d(TAG, "onCreate:" + android.os.Process.myPid());
        mHandler = new WebViewHandler(this);

        initDataAndTitleBar();
        initWebViewOptions();

        mPresenter = new WebTimeOutPresenter(this, mHandler);

        startToLoadWebUrl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWebViewClientAndWebChromeClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.d(TAG, "onDestroy");
        mPresenter.destoryTimer();

        try {
            if (mSystemWebView != null) {
                ViewParent parent = mSystemWebView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mSystemWebView);
                }
                mSystemWebView.stopLoading();
                mSystemWebView.removeAllViews();
                mSystemWebView.clearHistory();
                mSystemWebView.clearView();
                mSystemWebView.destroy();
                mSystemWebView = null;
            }

            if (mSystemWebViewEngine != null) {
                mSystemWebViewEngine.stopLoading();
                mSystemWebViewEngine.destroy();
                mSystemWebViewEngine.clearHistory();
                mSystemWebViewEngine.clearCache();
                mSystemWebViewEngine = null;
            }

            if (appView != null) {
                synchronized (appView) {
                    appView.notifyAll();//release thread wait in CordovaWebViewImpl
                }
                appView.stopLoading();

                //shutdown cordova threadpool for leak
                try {
                    Field f = appView.getClass().getDeclaredField("cordova");
                    if (f.isAccessible() == false) {
                        f.setAccessible(true);
                    }
                    Object obj_get = f.get(appView);
                    if (obj_get != null && obj_get instanceof CordovaInterface) {
                        CordovaInterface cordova = (CordovaInterface) obj_get;

                        ExecutorService threadPool = cordova.getThreadPool();
                        LogUtils.d(TAG, " threadPool.isShutdown=" + threadPool.isShutdown() + ",threadPool.isTerminated=" + threadPool.isTerminated());

                        threadPool.shutdown();
                        if (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                            List<Runnable> runnableList = threadPool.shutdownNow();
                            LogUtils.d(TAG, " threadPool.shutdownNow");
                        }

                        LogUtils.d(TAG, " threadPool.isShutdown=" + threadPool.isShutdown() + ",threadPool.isTerminated=" + threadPool.isTerminated());
                    }
                } catch (Exception ex) {
                    LogUtils.d(TAG, "getThreadPool,ex=" + ex.toString());
                }

                appView = null;
            }
        } catch (Exception ex) {
            LogUtils.d(TAG, "onDestroy, ex=" + ex.toString());
            ex.printStackTrace();
        }
    }

    public void initWebViewOptions() {
        mSystemWebViewEngine = new SystemWebViewEngine(mSystemWebView);
        mSystemWebView.getSettings().setJavaScriptEnabled(true);//支持js
        mSystemWebView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        mSystemWebView.getSettings().setSupportZoom(true);//支持缩放
        mSystemWebView.getSettings().setDomStorageEnabled(true);//设置可以使用localStorage
        mSystemWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mSystemWebView.getSettings().setAppCacheEnabled(true);//应用可以有缓存开启缓存模式
        String appCaceDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        mSystemWebView.getSettings().setAppCachePath(appCaceDir);
        //Log.d(TAG, "onCreate: " + Utils.isNetworkAvailable(DeviceAllWebActivity.this));
        if (NetworkManagerUtils.isNetworkAvailable(this)) {//联网情况下请求数据
            mSystemWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//缓存模式
        } else {//没联网情况走缓存
            mSystemWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
        }
        /*
        LOAD_CACHE_ONLY:  不使用网络，只读取本地缓存数据;
        LOAD_DEFAULT:  根据cache-control决定是否从网络上取数据;
        LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式;
        LOAD_NO_CACHE: 不使用缓存，只从网络获取数据;
        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据;
        */
    }

    public void initDataAndTitleBar() {
        Intent intent = getIntent();
        web_key = intent.getStringExtra(CommonWebActivity.WEB_KEY_STATUS);
        mWeb_url = intent.getStringExtra(CommonWebActivity.WEB_VALUE_URL);
//        if (web_key.equals(getString(R.string.common_issues))) {
//            //set title bar
//            mWeb_url = "file:///android_asset/commonissues/index.html";
//        }
        LogUtils.d(TAG, "web_key=" + web_key);
        LogUtils.d(TAG, "mWeb_url=" + mWeb_url);
        TitlebarUtils.initTitleBar(this, web_key);
    }

    public void setWebViewClientAndWebChromeClient() {
        mSystemWebView.setWebViewClient(new SystemWebViewClient(mSystemWebViewEngine) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 新版本，只会在Android6及以上调用
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                    showTimeOutView();
                }
            }

            // 旧版本，会在新版本中也可能被调用，所以加上一个判断，防止重复显示
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
                showTimeOutView();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: ");
                mProgressBar.setVisibility(View.VISIBLE);
                mPresenter.startTimerSchedule();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: ");
                if (appView != null) {
                    try {
                        synchronized (appView) {
                            appView.notifyAll();//release thread wait in CordovaWebViewImpl
                        }
                    } catch (Exception ex) {
                        LogUtils.d(TAG, "onPageFinished, ex=" + ex.toString());
                    }
                }
                mProgressBar.setVisibility(View.GONE);
                mPresenter.destoryTimer();
            }
        });

        mSystemWebView.setWebChromeClient(new SystemWebChromeClient(mSystemWebViewEngine) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
    }

    public void startToLoadWebUrl() {
        mWebViewLayout.setVisibility(View.VISIBLE);
        mNoNetWorkLayout.setVisibility(View.GONE);

        mPresenter.initTimer();//init timeout timer
        loadUrl(mWeb_url);
    }

    //当发生不可恢复的错误时，显示预定义的出错页面或错误信息
    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
        showTimeOutView();
    }

    @Override
    protected CordovaWebView makeWebView() {
        LogUtils.d(TAG, "makeWebView, mCordovaWebView");
        return new CordovaWebViewImpl(mSystemWebViewEngine);
    }

    @Override
    protected void createViews() {
        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.RED);
            // Background of activity:
            appView.getView().setBackgroundColor(backgroundColor);
        }
        appView.getView().requestFocusFromTouch();
    }

    private static class WebViewHandler extends Handler {
        private WeakReference<CommonWebActivity> mWeakReference;

        WebViewHandler(CommonWebActivity webView) {
            mWeakReference = new WeakReference<>(webView);
        }

        @Override
        public void handleMessage(Message msg) {
            CommonWebActivity webViewActivity = mWeakReference.get();
            if (webViewActivity != null) {
                webViewActivity.showTimeOutView();
            }
        }
    }

    @Override
    public void showTimeOutView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSystemWebView.stopLoading();
                mWebViewLayout.setVisibility(View.GONE);
                mNoNetWorkLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.tv_no_network)
    public void clickToReloadWebUrl() {
        startToLoadWebUrl();
    }
}
