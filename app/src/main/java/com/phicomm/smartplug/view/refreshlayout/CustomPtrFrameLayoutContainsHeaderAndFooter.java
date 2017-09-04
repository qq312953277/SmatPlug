package com.phicomm.smartplug.view.refreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by hk on 2017/1/11.
 */
public class CustomPtrFrameLayoutContainsHeaderAndFooter extends PtrFrameLayout {
    private CustomPtrHeader customPtrHeader;
    private CustomAdapter adapter;
    private final float pullUpdistance = 100;
    private boolean canPullUp = false;

    public CustomPtrFrameLayoutContainsHeaderAndFooter(Context context) {
        super(context);
        initViews();
        initGesture();
    }

    public CustomPtrFrameLayoutContainsHeaderAndFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        initGesture();
    }

    public CustomPtrFrameLayoutContainsHeaderAndFooter(Context context, AttributeSet attrs, int defStyle) {
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

    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent e) {
//        detector.onTouchEvent(e);
//        if (e.getAction() == MotionEvent.ACTION_UP) {
//            isFirst = true;
//            mIsHorizontalMode = false;
//            mIsDisallowIntercept = false;
//            return super.dispatchTouchEvent(e);
//        }
//        if (detector.onTouchEvent(e) && mIsDisallowIntercept && mIsHorizontalMode) {
//            return dispatchTouchEventSupper(e);
//        }
//        if (mIsHorizontalMode) {
//            return dispatchTouchEventSupper(e);
//        }
//        return super.dispatchTouchEvent(e);
//    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        detector.onTouchEvent(ev);
        // if(isLoading){ return  true;}
        if (customPtrHeader.isRefreshing()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //----------------------------------------------------------------
    private static OnLoadMoreListener loadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface OnLoadMoreListener {
        public void loadMore();
    }

    public void setAdapter(@NonNull RecyclerView recyclerView, @NonNull CustomAdapter mAdapter) {
        adapter = mAdapter;
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (loadMoreListener != null && !customPtrHeader.isRefreshing() && (adapter.getState() == CustomAdapter.STATE_MORE || adapter.getState() == CustomAdapter.STATE_ERROR)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && !ViewCompat.canScrollVertically(recyclerView, 1)) {
                    //当上拉距离到达一定距离时，再加载数据，而不是，稍微滑动下，就加载
                    if (canPullUp) {
                        adapter.setState(CustomAdapter.STATE_LOAIND);
                        loadMoreListener.loadMore();
                        canPullUp = false;
                    }

                }
            }
        });
    }

    /**
     * 加载完毕
     *
     * @param hasMore 是否还有下一页
     */
    public void loadComplete(boolean hasMore) {

        if (adapter == null) {
            throw new RuntimeException("must call method setAdapter to bind data");
        }
        adapter.setState(hasMore ? CustomAdapter.STATE_MORE : CustomAdapter.STATE_END);
        setEnabled(true);
    }

    /**
     * 加载出错
     */
    public void loadError() {
        if (adapter == null) {
            throw new RuntimeException("must call method setAdapter to bind data");
        }
        adapter.setState(CustomAdapter.STATE_ERROR);
    }

    /**
     * 支持加载更多的代理适配器
     */
    public static abstract class CustomAdapter extends RecyclerView.Adapter {
        static final int STATE_MORE = 0, STATE_LOAIND = 1, STATE_END = 2, STATE_ERROR = 3;
        int state = STATE_MORE;

        public void setState(int state) {
            if (this.state != state) {
                this.state = state;
                notifyItemChanged(getItemCount() - 1);
            }
        }

        public int getState() {
            return state;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return -99;
            }
            return getItemType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == -99) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(com.phicomm.smartplug.R.layout.loadmore_default_footer, parent, false)) {
                };
            } else {
                return onCreateItemHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == -99) {
                ProgressBar progressBar = (ProgressBar) holder.itemView.findViewById(com.phicomm.smartplug.R.id.loadmore_default_footer_progressbar);
                TextView textView = (TextView) holder.itemView.findViewById(com.phicomm.smartplug.R.id.loadmore_default_footer_tv);
                if (state == STATE_END) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText(com.phicomm.smartplug.R.string.refershlayout_loading_no_more);
                } else if (state == STATE_MORE) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText(com.phicomm.smartplug.R.string.refershlayout_loading_more);
                } else if (state == STATE_LOAIND) {
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText(com.phicomm.smartplug.R.string.refershlayout_is_loading);
                } else if (state == STATE_ERROR) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText(com.phicomm.smartplug.R.string.refershlayout_loading_fail);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loadMoreListener != null && (state == STATE_MORE || state == STATE_ERROR)) {
                            setState(STATE_LOAIND);
                            loadMoreListener.loadMore();
                        }
                    }
                });
            } else {
                onBindItemHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            return getCount() == 0 ? 0 : getCount() + 1;
        }

        public int getItemType(int positiion) {
            return super.getItemViewType(positiion);
        }

        public abstract RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType);

        public abstract void onBindItemHolder(RecyclerView.ViewHolder holder, int position);

        public abstract int getCount();

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            // 处理瀑布流模式 最后的 item 占整行
            if (holder.getLayoutPosition() == getItemCount() - 1) {
                ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            //处理网格布局模式，最后的item占整行
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
                final int lastSpanCount = gridLayoutManager.getSpanCount();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position == getItemCount() - 1 ? lastSpanCount :
                                (spanSizeLookup == null ? 1 : spanSizeLookup.getSpanSize(position));
                    }
                });
            }
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
//

//    /**
//     * 对于底层的View来说，有一种方法可以阻止父层的View截获touch事件，就是调用getParent().requestDisallowInterceptTouchEvent(true);方法。
//     * 一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action。
//     *
//     * */
//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        this.mIsDisallowIntercept = disallowIntercept;
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
//    }
//

    //
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

            //当上拉距离到达一定距离时，再加载数据，而不是，稍微滑动下，就加载
            if (distanceY > 100) {
                canPullUp = true;
            }
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
