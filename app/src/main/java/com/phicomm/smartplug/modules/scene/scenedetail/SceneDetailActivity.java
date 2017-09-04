package com.phicomm.smartplug.modules.scene.scenedetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.DeleteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.scene.addscene.ui.AddTaskActivity;
import com.phicomm.smartplug.modules.scene.addscene.ui.AddTriggerActivity;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.modules.scene.utils.TaskComparator;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.LogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;

import java.util.Collections;
import java.util.Date;

import butterknife.BindView;

public class SceneDetailActivity extends BaseActivity implements SceneDetailContract.View {
    private final String TAG = SceneDetailActivity.this.getClass().getSimpleName();

    @BindView(R.id.scene_detail_name_layout)
    LinearLayout mSceneNameLayout;

    @BindView(R.id.scene_title)
    EditText mSceneTitle;

    @BindView(R.id.scene_detail_trigger_prompt)
    TextView mTriggerPrompt;

    @BindView(R.id.scene_detail_task_prompt)
    TextView mTaskPrompt;

    @BindView(R.id.scene_detail_add_task)
    ImageView mAddTask;

    @BindView(R.id.scene_detail_button)
    Button mButton;

    @BindView(R.id.scene_detail_trigger_layout)
    RelativeLayout mTriggerLayout;

    @BindView(R.id.scene_detail_goto_detail)
    ImageView mGotoDetail;

    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.scene_detail_manually_trigger_layout)
    LinearLayout mManuallyTriggerLayout;

    @BindView(R.id.scene_detail_timer_trigger_layout)
    LinearLayout mTimerTriggerLayout;

    @BindView(R.id.scene_detail_timer_detail)
    TextView mAddSceneTimerDetail;

    @BindView(R.id.scene_detail_timer_repeat_detail)
    TextView mAddSceneTimerRepeatDetail;

    @BindView(R.id.scene_detail_task_list)
    RecyclerView mTaskList;

    private SceneDetailTaskAdapter mTaskAdapter;

    private SceneDetailPresenter mPresenter;

    private SceneModel mScene;

    private boolean mIsEditMode = false;
    private boolean mIsDirty = false;
    private String mSceneOriginalName = "";

    private long mSceneId = 0;

    private static final int MAX_TASK_COUNT = 20;

    // scene refresh
    private Handler mHandler = new Handler();
    private boolean isSceneDetailVisible = false;
    private static final int TIMER_SCENE_REFRESH_INTERVAL = 5000;
    private static final int MANUALLY_SCENE_REFRESH_INTERVAL = 5000;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            getSceneDetail();
        }
    };

    // delete scene
    View.OnClickListener mDeleteSceneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDeleteSceneDialog();
        }
    };

    // execute scene
    View.OnClickListener mExecuteSceneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogUtils.showProgressDialog(SceneDetailActivity.this);
            mPresenter.executeScene(mScene.getSceneId());

            // umeng
            if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "execute_manual_scene_in_detail");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "execute_timer_scene_in_detail");
            }
        }
    };

    // cancel scene
    View.OnClickListener mCancelSceneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogUtils.showProgressDialog(SceneDetailActivity.this);
            mPresenter.cancelScene(mScene.getSceneId());

            // umeng
            if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "cancel_manual_scene_in_detail");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "cancel_timer_scene_in_detail");
            }
        }
    };

    // request code
    private final static int REQUEST_TRIGGER_CODE = 110;
    private final static int REQUEST_TASK_CODE = 111;

    // edit action
    private PhiTitleBar.Action mEditAction = new PhiTitleBar.Action() {
        @Override
        public String getText() {
            return getResources().getString(R.string.edit);
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public void performAction(View view) {
            enterEditMode();
        }
    };

    // modify action
    private PhiTitleBar.Action mModifyAction = new PhiTitleBar.Action() {
        @Override
        public String getText() {
            return getResources().getString(R.string.save);
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public void performAction(View view) {
            performEdit();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detail);

        initView();
        initPresenter();

        if (NetworkManagerUtils.isNetworkAvailable(getApplicationContext())) {
            DialogUtils.showProgressDialog(this);
            getSceneDetail();
        } else {
            showToast(R.string.network_unavailable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSceneDetailVisible = true;
        refreshSceneDetail();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isSceneDetailVisible = false;
        cancelRefreshSceneDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isSceneDetailVisible = false;
        cancelRefreshSceneDetail();
    }

    private void initView() {
        // setup title
        TitlebarUtils.initTitleBar(this, mTitleBar, R.string.scene_detail);
        mTitleBar.setLeftImageResource(R.drawable.button_back);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditMode) {
                    if ((mIsDirty || mTaskAdapter.isSomeTaskDeleted())
                            && mSceneTitle.getText() != null
                            && mSceneTitle.getText().length() > 0
                            && mTaskAdapter.getData().size() > 0) {
                        showExitDialog();
                    } else {
                        if (mSceneTitle.getText() == null
                                || mSceneTitle.getText().length() == 0
                                || mTaskAdapter.getData().size() == 0) {
                            finish();
                        } else {
                            exitEditMode();
                        }
                    }
                } else {
                    finish();
                }
            }
        });

        // task list
        mTaskList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTaskAdapter = new SceneDetailTaskAdapter(this);
        mTaskList.setAdapter(mTaskAdapter);

        mSceneId = getIntent().getLongExtra("scene_id", 0);
    }

    private void getSceneDetail() {
        Log.d(TAG, "getSceneDetail");
        mPresenter.getSceneDetail(mSceneId);
    }

    private void initPresenter() {
        mPresenter = new SceneDetailPresenter(this, this);
    }

    private void enterEditMode() {
        mIsEditMode = true;

        // change title
        mTitleBar.removeAllActions();
        mTitleBar.addAction(mModifyAction);
        mTitleBar.setActionTextColor(R.color.white);
        mTitleBar.setTitle(R.string.edit_scene);

        // display scene name layout
        mSceneNameLayout.setVisibility(View.VISIBLE);
        mSceneTitle.setText(mScene.getSceneName());
        mSceneTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // here check if title has been changed
                if (mSceneOriginalName != null
                        && mSceneOriginalName.length() > 0 && s != null
                        && s.length() > 0
                        && !mSceneOriginalName.equals(s.toString())) {
                    mIsDirty = true;
                } else {
                    mIsDirty = false;
                }

                // check special char
                if (s.length() > 0 && CommonUtils.hasSpecialCharacters(s.toString())) {
                    showToast(R.string.please_input_regular_character);
                }
            }
        });

        // display trigger layout
        mTriggerPrompt.setVisibility(View.VISIBLE);
        mGotoDetail.setVisibility(View.VISIBLE);
        mTriggerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SceneDetailActivity.this, AddTriggerActivity.class);
                if (mScene != null) {
                    intent.putExtra("trigger", mScene.getTrigger());
                }
                startActivityForResult(intent, REQUEST_TRIGGER_CODE);
            }
        });

        // display task prompt
        mTaskPrompt.setVisibility(View.VISIBLE);

        // display add task
        mAddTask.setVisibility(View.VISIBLE);
        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTaskAdapter != null && mTaskAdapter.getData().size() >= MAX_TASK_COUNT) {
                    showToast(R.string.max_task_alert);
                } else {
                    Intent intent = new Intent(SceneDetailActivity.this, AddTaskActivity.class);
                    startActivityForResult(intent, REQUEST_TASK_CODE);
                }
            }
        });

        // update task list ui
        mTaskAdapter.setEditMode(true);
        mTaskAdapter.notifyDataSetChanged();

        // change button
        mButton.setText(R.string.delete_scene);
        mButton.setOnClickListener(mDeleteSceneOnClickListener);
    }

    private void exitEditMode() {
        // reset edit mode flag
        mIsEditMode = false;

        // hide scene name layout
        mSceneNameLayout.setVisibility(View.GONE);

        // setup title
        mTitleBar.setTitle(mSceneOriginalName);
        mTitleBar.removeAllActions();
        mTitleBar.setActionTextColor(R.color.white);
        mTitleBar.addAction(mEditAction);

        // hide trigger layout
        mTriggerPrompt.setVisibility(View.GONE);
        mGotoDetail.setVisibility(View.GONE);
        mTriggerLayout.setOnClickListener(null);

        // update task list ui
        mTaskAdapter.setEditMode(false);
        mTaskAdapter.notifyDataSetChanged();

        // hide add task
        mAddTask.setVisibility(View.GONE);

        // update button
        mButton.setText(R.string.execute_scene);
        mButton.setOnClickListener(mExecuteSceneOnClickListener);
    }

    private void performEdit() {
        if (mTaskAdapter.getData().size() == 0) {
            showToast(R.string.please_add_task);
            return;
        }

        if (mTaskAdapter.getData().size() > MAX_TASK_COUNT) {
            showToast(R.string.max_task_alert);
            return;
        }

        if (mSceneTitle.getText().toString().length() == 0) {
            showToast(R.string.please_input_scene_title);
            return;
        }

        if (CommonUtils.hasSpecialCharacters(mSceneTitle.getText().toString())) {
            showToast(R.string.please_input_regular_character);
            return;
        }

        // update name
        mScene.setSceneName(mSceneTitle.getText().toString());
        mScene.setSceneDescription(mSceneTitle.getText().toString());

        // assign task id
        for (int i = 0; i < mScene.getTasks().size(); i++) {
            mScene.getTasks().get(i).setTaskId(i + 1);
        }

        // enable scene default
        if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER) {
            mScene.setEnabled(true);
        }

        if (NetworkManagerUtils.isNetworkAvailable(this)) {
            DialogUtils.showProgressDialog(SceneDetailActivity.this);
            mPresenter.editScene(mScene);

            // umeng
            if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "edit_manual_scene");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "edit_timer_scene");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TRIGGER_CODE) {
                TriggerModel trigger = (TriggerModel) data.getSerializableExtra("trigger");
                if (trigger != null) {
                    mScene.setTrigger(trigger);
                    updateTrigger(trigger);

                    // trigger has been modified
                    mIsDirty = true;
                }
            } else if (requestCode == REQUEST_TASK_CODE) {
                TaskModel taskModel = (TaskModel) data.getSerializableExtra("task");
                if (taskModel != null) {
                    // update ui
                    mTaskAdapter.addData(taskModel);
                    Collections.sort(mTaskAdapter.getData(), new TaskComparator());
                    mTaskAdapter.notifyDataSetChanged();

                    // task has been modified
                    mIsDirty = true;
                }
            }
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

    @Override
    public void updateSceneDetailView(SceneModel scene, long currServerTime) {
        if (scene != null && !mIsEditMode) {
            mScene = scene;

            mTaskAdapter.setScene(mScene);

            // cache scene name before edit
            mSceneOriginalName = scene.getSceneName();

            // update title
            mTitleBar.setTitle(scene.getSceneName());
            mTitleBar.removeAllActions();
            if (!scene.getStatus()) {
                mTitleBar.setActionTextColor(R.color.white);
                mTitleBar.addAction(mEditAction);
            }

            // update trigger
            updateTrigger(mScene.getTrigger());

            // update button
            mButton.setVisibility(View.VISIBLE);
            if (!mScene.getStatus()) {
                mButton.setText(R.string.execute_scene);
                mButton.setOnClickListener(mExecuteSceneOnClickListener);
            } else {
                mButton.setText(R.string.cancel_scene);
                mButton.setOnClickListener(mCancelSceneOnClickListener);
            }

            // display task prompt
            mTaskPrompt.setVisibility(View.VISIBLE);

            // update task list
            mTaskAdapter.setNewData(mScene.getTasks());

            // update server time
            try {
                Date currServerDate = new Date(currServerTime * 1000);
                mTaskAdapter.setCurrentServerDate(currServerDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // launch refresh scene detail request
            if (mScene.getStatus()) {
                if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                    refreshSceneDetailDelayed(MANUALLY_SCENE_REFRESH_INTERVAL);
                } else if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER) {
                    refreshSceneDetailDelayed(TIMER_SCENE_REFRESH_INTERVAL);
                }
            }
        }
    }

    private void updateTrigger(TriggerModel trigger) {

        mTriggerLayout.setVisibility(View.VISIBLE);

        if (trigger.getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
            mManuallyTriggerLayout.setVisibility(View.VISIBLE);
            mTimerTriggerLayout.setVisibility(View.GONE);
        } else {
            mManuallyTriggerLayout.setVisibility(View.GONE);
            mTimerTriggerLayout.setVisibility(View.VISIBLE);

            String hour = (trigger.getTriggerTime().getHour() >= 10) ? ("" + trigger.getTriggerTime().getHour()) : ("0" + trigger.getTriggerTime().getHour());
            String minute = (trigger.getTriggerTime().getMinute() >= 10) ? ("" + trigger.getTriggerTime().getMinute()) : ("0" + trigger.getTriggerTime().getMinute());
            mAddSceneTimerDetail.setText(hour + ":" + minute);

            // get repeat days string
            if (trigger.getTriggerTime().getRepeatDays() != null && trigger.getTriggerTime().getRepeatDays().size() > 0) {
                if (trigger.getTriggerTime().getRepeatDays().size() == 7) {
                    mAddSceneTimerRepeatDetail.setText(getResources().getString(R.string.timer_repeat_every_day));
                } else {
                    StringBuilder sb = new StringBuilder();
                    int length = trigger.getTriggerTime().getRepeatDays().size();
                    for (int i = 0; i < length; i++) {
                        int day = trigger.getTriggerTime().getRepeatDays().get(i);
                        sb.append(getResources().getStringArray(R.array.week)[day - 1]);
                        if (i < length - 1) {
                            sb.append(getResources().getString(R.string.timer_repeat_day_divider));
                        }
                    }
                    mAddSceneTimerRepeatDetail.setText(sb);
                }
                mAddSceneTimerRepeatDetail.setVisibility(View.VISIBLE);
            } else {
                mAddSceneTimerRepeatDetail.setVisibility(View.VISIBLE);
                mAddSceneTimerRepeatDetail.setText(getResources().getString(R.string.timer_repeat_once));
            }
        }
    }

    @Override
    public void cancelLoadingView() {
        DialogUtils.dismissDialog();
    }

    @Override
    public void analysisDeleteSceneResponseBean(DeleteSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                Intent intent = new Intent();
                intent.putExtra("reload", true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analysisExecuteSceneResponseBean(ExecuteSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                // need refresh scene list
                Intent intent = new Intent();
                intent.putExtra("reload", true);
                setResult(Activity.RESULT_OK, intent);

                // notify user
                showToast(R.string.scene_has_been_launched);

                // refresh scene
                refreshSceneDetail();
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
                // need refresh scene list
                Intent intent = new Intent();
                intent.putExtra("reload", true);
                setResult(Activity.RESULT_OK, intent);

                // notify user
                showToast(R.string.scene_has_been_canceled);

                // we should refresh ui
                refreshSceneDetail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analysisEditSceneResponseBean(EditSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                exitEditMode();

                // need refresh scene list
                Intent intent = new Intent();
                intent.putExtra("reload", true);
                setResult(Activity.RESULT_OK, intent);

                refreshSceneDetail();
            } else {
                showToast(bean.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDeleteSceneDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_common, null);
        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        message.setText(R.string.delete_scene_query);
        final PhiGuideDialog dialog = new PhiGuideDialog(this);
        dialog.setContentPanel(view);
        dialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                dialog.dismiss();
            }
        });

        dialog.setRightGuideOnclickListener(getResources().getString(R.string.ok), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                dialog.dismiss();
                DialogUtils.showProgressDialog(SceneDetailActivity.this);
                mPresenter.deleteScene(mScene.getSceneId());

                // umeng
                if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                    DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "delete_manual_scene");
                } else {
                    DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "delete_timer_scene");
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mIsEditMode) {
            if ((mIsDirty || mTaskAdapter.isSomeTaskDeleted())
                    && mSceneTitle.getText() != null
                    && mSceneTitle.getText().length() > 0
                    && mTaskAdapter.getData().size() > 0) {
                showExitDialog();
            } else {
                if (mSceneTitle.getText() == null
                        || mSceneTitle.getText().length() == 0
                        || mTaskAdapter.getData().size() == 0) {
                    finish();
                } else {
                    exitEditMode();
                }
            }
        } else {
            finish();
        }
    }

    private void refreshSceneDetailDelayed(int interval) {
        Log.d(TAG, "refreshSceneDetailDelayed interval=" + interval);

        if (isSceneDetailVisible) {
            // remove former request
            mHandler.removeCallbacks(mRunnable);

            // call refresh after some time
            mHandler.postDelayed(mRunnable, interval);
        }
    }

    private void refreshSceneDetail() {
        Log.d(TAG, "refreshSceneDetail");

        if (isSceneDetailVisible && NetworkManagerUtils.isNetworkAvailable(getApplicationContext())) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.post(mRunnable);
        }
    }

    private void cancelRefreshSceneDetail() {
        LogUtils.d(TAG, "cancelRefreshSceneDetail");

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void showExitDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_common, null);
        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        message.setText(R.string.quit_save_scene_query);
        final PhiGuideDialog dialog = new PhiGuideDialog(this);
        dialog.setContentPanel(view);
        dialog.setLeftGuideOnclickListener(getResources().getString(R.string.cancel), R.color.syn_text_color, new PhiGuideDialog.onLeftGuideOnclickListener() {
            @Override
            public void onLeftGuideClick() {
                dialog.dismiss();
            }
        });

        dialog.setRightGuideOnclickListener(getResources().getString(R.string.ok), R.color.weight_line_color, new PhiGuideDialog.onRightGuideOnclickListener() {
            @Override
            public void onRightGuideClick() {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}
