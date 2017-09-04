package com.phicomm.smartplug.modules.scene.scenemain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseFragment;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneListResponseBean;
import com.phicomm.smartplug.modules.scene.addscene.ui.AddSceneActivity;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.modules.scene.scenedetail.SceneDetailActivity;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.refreshlayout.CustomPtrFrameLayoutRefreshHeader;

import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class SceneFragment extends BaseFragment implements SceneContract.View, BaseQuickAdapter.OnItemClickListener, ExecuteSceneInterface {
    private final String TAG = SceneFragment.this.getClass().getSimpleName();

    @BindView(R.id.scene_list)
    RecyclerView mRecyclerViewSceneList;

    @BindView(R.id.frame_scene)
    CustomPtrFrameLayoutRefreshHeader mFrameScene;

    private SceneListsAdapter mSceneListsAdapter;

    private SceneContract.Presenter mScenePresenter;

    private Handler mHandler = new Handler();

    private boolean isSceneFragmentVisible = false;

    private static final int SCENE_REFRESH_DELAY_MILLISECOND = 5000;

    private static final int REQUEST_RELOAD_CODE = 120;

    private long formerExecuteTime = 0;
    private static final int ANTI_DOUBLE_CLICK_INTERVAL = 800;

    private Runnable refreshSceneListRunnable = new Runnable() {
        public void run() {
            getSceneList();
        }
    };

    private Runnable resetFrameLayoutRefreshHeaderRunnable = new Runnable() {
        public void run() {
            setRefreshComplete();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene, null);
        initTitleView(view);
        return view;
    }

    private void initTitleView(View mainView) {
        com.phicomm.widgets.PhiTitleBar phiTitleBar = (com.phicomm.widgets.PhiTitleBar) mainView.findViewById(R.id.title_bar);
        TitlebarUtils.initTitleBar(this.getActivity(), phiTitleBar, R.string.scene);
        phiTitleBar.addAction(new com.phicomm.widgets.PhiTitleBar.Action() {
            @Override
            public String getText() {
                return null;
            }

            @Override
            public int getDrawable() {
                return R.drawable.title_add_icon;
            }

            @Override
            public void performAction(View view) {
                Intent intent = new Intent(getActivity(), AddSceneActivity.class);
                startActivityForResult(intent, REQUEST_RELOAD_CODE);

                // umeng
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "add_scene");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RELOAD_CODE) {
            if (data != null) {
                boolean reloadSceneList = data.getBooleanExtra("reload", false);
                if (reloadSceneList) {
                    getSceneList();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView(savedInstanceState);
        initRefresh();
        initPresenter();
        getSceneList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initView(@Nullable Bundle savedInstanceState) {
        // divider
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        mRecyclerViewSceneList.addItemDecoration(dividerDecoration);

        // adapter
        mRecyclerViewSceneList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSceneListsAdapter = new SceneListsAdapter(getActivity());
        mSceneListsAdapter.setOnItemClickListener(this);
        mRecyclerViewSceneList.setAdapter(mSceneListsAdapter);

        // set interface
        mSceneListsAdapter.setmSceneInterface(this);

        // set empty view
        View emptyView = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_scene_empty_view, (ViewGroup) mRecyclerViewSceneList.getParent(), false);
        mSceneListsAdapter.setEmptyView(emptyView);
    }

    private void initRefresh() {
        mFrameScene.setLastUpdateTimeRelateObject(this);
        mFrameScene.setPtrHandler(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                refreshSceneList();

                mHandler.postDelayed(resetFrameLayoutRefreshHeaderRunnable, SCENE_REFRESH_DELAY_MILLISECOND);
            }
        });
    }

    private void initPresenter() {
        mScenePresenter = new ScenePresenter(getActivity(), this);
    }

    private void getSceneList() {
        LogUtils.d(TAG, "getSceneList");

        if (mScenePresenter != null) {
            mScenePresenter.getSceneLists();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelRefreshSceneList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            isSceneFragmentVisible = true;

            refreshSceneListDelayed();
        } else {
            isSceneFragmentVisible = false;

            cancelRefreshSceneList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        cancelRefreshSceneList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelRefreshSceneList();
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.d(TAG, "onResume isSceneFragmentVisible=" + isSceneFragmentVisible);
        refreshSceneList();
    }

    @Override
    public void analysisResponseBean(GetSceneListResponseBean bean) {
        setRefreshComplete();
    }

    @Override
    public void updateSceneList(List<SceneModel> sceneList) {
        mSceneListsAdapter.setNewData(sceneList);

        // launch a new refresh request
        refreshSceneListDelayed();
    }

    private void refreshSceneListDelayed() {
        if (mSceneListsAdapter != null
                && mSceneListsAdapter.getData().size() > 0
                && isSceneFragmentVisible) {
            // remove former request
            mHandler.removeCallbacks(refreshSceneListRunnable);

            // call refresh after some time
            mHandler.postDelayed(refreshSceneListRunnable, SCENE_REFRESH_DELAY_MILLISECOND);
        }
    }

    private void refreshSceneList() {
        LogUtils.d(TAG, "refreshSceneList size=" + mSceneListsAdapter.getData().size() + " && isSceneFragmentVisible=" + isSceneFragmentVisible);
        if (mSceneListsAdapter != null
                && mSceneListsAdapter.getData().size() > 0
                && isSceneFragmentVisible) {
            mHandler.removeCallbacks(refreshSceneListRunnable);
            mHandler.post(refreshSceneListRunnable);
        }
    }

    private void cancelRefreshSceneList() {
        LogUtils.d(TAG, "cancelRefreshSceneList");
        if (mHandler != null) {
            mHandler.removeCallbacks(refreshSceneListRunnable);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SceneModel scene = mSceneListsAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), SceneDetailActivity.class);
        intent.putExtra("scene_id", scene.getSceneId());
        startActivityForResult(intent, REQUEST_RELOAD_CODE);

        // umeng
        DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "get_scene_detail");
    }

    @Override
    public void setRefreshComplete() {
        mFrameScene.refreshComplete();
        mHandler.removeCallbacks(resetFrameLayoutRefreshHeaderRunnable);
    }

    @Override
    public void executeScene(boolean run, SceneModel scene, int triggerType) {
        if (run && scene.getStatus()) {
            if (triggerType == TriggerModel.TRIGGER_MANUALLY) {
                showToast(R.string.scene_is_running);
            }
            return;
        }

        if (!run && !scene.getStatus()) {
            return;
        }

        // anti double switch
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - formerExecuteTime < ANTI_DOUBLE_CLICK_INTERVAL) {
            formerExecuteTime = currentTime;
            showToast(R.string.scene_switch_too_fast);
            return;
        }
        formerExecuteTime = currentTime;

        DialogUtils.showProgressDialog(getContext());

        if (run) {
            mScenePresenter.executeScene(scene.getSceneId());

            // umeng
            if (triggerType == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "execute_manual_scene_in_list");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "execute_timer_scene_in_list");
            }
        } else {
            mScenePresenter.cancelScene(scene.getSceneId());

            // umeng
            if (triggerType == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "cancel_manual_scene_in_list");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "cancel_timer_scene_in_list");
            }
        }
    }

    @Override
    public void cancelLoadingView() {
        DialogUtils.dismissDialog();
    }

    @Override
    public void analysisExecuteSceneResponseBean(ExecuteSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                // notify user
                showToast(R.string.scene_has_been_launched);

                // refresh scene
                getSceneList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analysisCancelSceneResponseBean(CancelSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                // notify user
                showToast(R.string.scene_has_been_canceled);

                // refresh scene
                getSceneList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
