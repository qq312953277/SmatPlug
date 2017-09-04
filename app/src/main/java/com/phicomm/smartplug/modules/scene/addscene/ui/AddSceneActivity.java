package com.phicomm.smartplug.modules.scene.addscene.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackAgent;
import com.phicomm.smartplug.modules.data.DataTracker.DataTrackerConfig;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneResponseBean;
import com.phicomm.smartplug.modules.scene.addscene.adapter.TaskAdapter;
import com.phicomm.smartplug.modules.scene.addscene.contract.AddSceneContract;
import com.phicomm.smartplug.modules.scene.addscene.presenter.AddScenePresenter;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;
import com.phicomm.smartplug.modules.scene.model.TimeModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.modules.scene.utils.TaskComparator;
import com.phicomm.smartplug.utils.CommonUtils;
import com.phicomm.smartplug.utils.DialogUtils;
import com.phicomm.smartplug.utils.NetworkManagerUtils;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;
import com.phicomm.widgets.alertdialog.PhiGuideDialog;

import java.util.Calendar;
import java.util.Collections;

import butterknife.BindView;

public class AddSceneActivity extends BaseActivity implements AddSceneContract.View {

    @BindView(R.id.add_scene_trigger_layout)
    RelativeLayout mTriggerLayout;

    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.scene_title)
    EditText mSceneTitle;

    @BindView(R.id.add_task)
    ImageView mAddTask;

    @BindView(R.id.add_scene_manually_trigger_layout)
    LinearLayout mManuallyTriggerLayout;

    @BindView(R.id.add_scene_timer_trigger_layout)
    LinearLayout mTimerTriggerLayout;

    @BindView(R.id.add_scene_timer_detail)
    TextView mAddSceneTimerDetail;

    @BindView(R.id.add_scene_timer_repeat_detail)
    TextView mAddSceneTimerRepeatDetail;

    @BindView(R.id.task_list)
    RecyclerView mTaskList;

    // used for mTrigger condition
    private TriggerModel mTrigger;

    private TaskAdapter mTaskAdapter;

    private SceneModel mScene = new SceneModel();

    private AddScenePresenter mPresenter;

    // request code
    private final static int REQUEST_CONDITION_CODE = 110;
    private final static int REQUEST_TASK_CODE = 111;

    private final static int MAX_TASK_COUNT = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add_scene);

        initView();
        initPresenter();
        initTrigger();
    }

    private void initView() {
        // setup title
        TitlebarUtils.initTitleBar(this, mTitleBar, R.string.add_scene);
        mTitleBar.setLeftImageResource(R.drawable.button_back);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSceneTitle.getText() != null && mSceneTitle.getText().length() > 0 && mTaskAdapter.getData().size() > 0) {
                    showExitDialog();
                } else {
                    finish();
                }
            }
        });
        mTitleBar.setActionTextColor(R.color.white);
        mTitleBar.addAction(new PhiTitleBar.Action() {
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
                performSave();
            }
        });

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
                if (s.length() > 0 && CommonUtils.hasSpecialCharacters(s.toString())) {
                    showToast(R.string.please_input_regular_character);
                }
            }
        });

        // set mTrigger condition
        mTriggerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSceneActivity.this, AddTriggerActivity.class);
                intent.putExtra("trigger", mTrigger);
                AddSceneActivity.this.startActivityForResult(intent, REQUEST_CONDITION_CODE);
            }
        });

        // task list
        mTaskList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTaskAdapter = new TaskAdapter(this);
        mTaskList.setAdapter(mTaskAdapter);

        // add task button
        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTaskAdapter != null && mTaskAdapter.getData().size() >= MAX_TASK_COUNT) {
                    showToast(R.string.max_task_alert);
                } else {
                    Intent intent = new Intent(AddSceneActivity.this, AddTaskActivity.class);
                    AddSceneActivity.this.startActivityForResult(intent, REQUEST_TASK_CODE);
                }
            }
        });
    }

    private void initPresenter() {
        mPresenter = new AddScenePresenter(this, this);
    }

    private void performSave() {
        mScene.setTrigger(mTrigger);
        mScene.setTasks(mTaskAdapter.getData());
        mScene.setSceneName(mSceneTitle.getText().toString());

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

        // assign task id
        for (int i = 0; i < mScene.getTasks().size(); i++) {
            mScene.getTasks().get(i).setTaskId(i + 1);
        }

        // enable scene default
        if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER) {
            mScene.setEnabled(true);
        }

        if (NetworkManagerUtils.isNetworkAvailable(this)) {
            DialogUtils.showProgressDialog(this);
            mPresenter.createScene(mScene);

            // umeng
            if (mScene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "create_manual_scene");
            } else {
                DataTrackAgent.commitCountEvent2Umeng(myActivity, DataTrackerConfig.EVENT_SCENE, "type", "create_timer_scene");
            }
        }
    }

    private void initTrigger() {
        TimeModel time = new TimeModel();
        Calendar calendar = Calendar.getInstance();
        time.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        time.setMinute(calendar.get(Calendar.MINUTE));

        mTrigger = new TriggerModel();
        mTrigger.setTriggerType(TriggerModel.TRIGGER_MANUALLY);
        mTrigger.setImageId(R.drawable.scene_manually_trigger_icon);
        mTrigger.setTriggerDescription(R.string.trigger_manually);
        mTrigger.setTriggerTime(time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CONDITION_CODE) {
                mTrigger = (TriggerModel) data.getSerializableExtra("trigger");
                if (mTrigger == null) {
                    mTrigger = new TriggerModel();
                }
                updateTrigger();
            } else if (requestCode == REQUEST_TASK_CODE) {
                TaskModel taskModel = (TaskModel) data.getSerializableExtra("task");
                if (taskModel != null) {
                    // update ui
                    mTaskAdapter.addData(taskModel);

                    Collections.sort(mTaskAdapter.getData(), new TaskComparator());
                    mTaskAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void updateTrigger() {
        if (mTrigger.getTriggerType() == TriggerModel.TRIGGER_MANUALLY) {
            mManuallyTriggerLayout.setVisibility(View.VISIBLE);
            mTimerTriggerLayout.setVisibility(View.GONE);
        } else {
            mManuallyTriggerLayout.setVisibility(View.GONE);
            mTimerTriggerLayout.setVisibility(View.VISIBLE);

            String hour = (mTrigger.getTriggerTime().getHour() >= 10) ? ("" + mTrigger.getTriggerTime().getHour()) : ("0" + mTrigger.getTriggerTime().getHour());
            String minute = (mTrigger.getTriggerTime().getMinute() >= 10) ? ("" + mTrigger.getTriggerTime().getMinute()) : ("0" + mTrigger.getTriggerTime().getMinute());
            mAddSceneTimerDetail.setText(hour + ":" + minute);

            // get repeat days string
            if (mTrigger.getTriggerTime().getRepeatDays() != null && mTrigger.getTriggerTime().getRepeatDays().size() > 0) {
                if (mTrigger.getTriggerTime().getRepeatDays().size() == 7) {
                    mAddSceneTimerRepeatDetail.setText(getResources().getString(R.string.timer_repeat_every_day));
                } else {
                    StringBuilder sb = new StringBuilder();
                    int length = mTrigger.getTriggerTime().getRepeatDays().size();
                    for (int i = 0; i < length; i++) {
                        int day = mTrigger.getTriggerTime().getRepeatDays().get(i);
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
    public void analysisResponseBean(CreateSceneResponseBean bean) {
        try {
            int errorCode = Integer.parseInt(bean.getError());
            if (errorCode == 0) {
                Intent intent = new Intent();
                intent.putExtra("reload", true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                showToast(bean.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showExitDialog() {
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

    @Override
    public void onBackPressed() {
        if (mSceneTitle.getText() != null && mSceneTitle.getText().length() > 0 && mTaskAdapter.getData().size() > 0) {
            showExitDialog();
        } else {
            finish();
        }
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }
}
