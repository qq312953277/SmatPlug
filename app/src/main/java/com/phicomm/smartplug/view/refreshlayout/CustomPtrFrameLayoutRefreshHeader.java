package com.phicomm.smartplug.view.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by hk on 2017/1/6.
 */
public class CustomPtrFrameLayoutRefreshHeader extends PtrFrameLayout {
    private CustomPtrHeader customPtrHeader;

    public CustomPtrFrameLayoutRefreshHeader(Context context) {
        super(context);
        initViews();
        initGesture();
    }

    public CustomPtrFrameLayoutRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        initGesture();
    }

    public CustomPtrFrameLayoutRefreshHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
        initGesture();
    }

    private void initViews() {
        customPtrHeader = new CustomPtrHeader(getContext());
        setHeaderView(customPtrHeader);
        addPtrUIHandler(customPtrHeader);
    }

    public CustomPtrHeader getHeader() {
        return customPtrHeader;
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (customPtrHeader != null) {
            customPtrHeader.setLastUpdateTimeKey(key);
        }
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        if (customPtrHeader != null) {
            customPtrHeader.setLastUpdateTimeRelateObject(object);
        }
    }

    private GestureDetector detector;
    private boolean mIsDisallowIntercept = false;

    private void initGesture() {
        detector = new GestureDetector(getContext(), gestureListener);
    }

    private boolean mIsHorizontalMode = false;
    private boolean isFirst = true;
    private boolean isLoading = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        detector.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            isFirst = true;
            mIsHorizontalMode = false;
            mIsDisallowIntercept = false;
            return super.dispatchTouchEvent(e);
        }
        if (detector.onTouchEvent(e) && mIsDisallowIntercept && mIsHorizontalMode) {
            return dispatchTouchEventSupper(e);
        }
        if (mIsHorizontalMode) {
            return dispatchTouchEventSupper(e);
        }
        return super.dispatchTouchEvent(e);
    }

    /**
     * 对于底层的View来说，有一种方法可以阻止父层的View截获touch事件，就是调用getParent().requestDisallowInterceptTouchEvent(true);方法。
     * 一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action。
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        this.mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // if(isLoading){ return  true;}
        if (customPtrHeader.isRefreshing()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float disX, disY;
            disX = Math.abs(distanceX);
            disY = Math.abs(distanceY);

            if (disX > disY) {
                if (isFirst) {
                    mIsHorizontalMode = true;
                    isFirst = false;
                }
            } else {
                if (isFirst) {
                    mIsHorizontalMode = false;
                    isFirst = false;
                }
                return false;//垂直滑动会返回false
            }

            return true;//水平滑动会返回true
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
}
