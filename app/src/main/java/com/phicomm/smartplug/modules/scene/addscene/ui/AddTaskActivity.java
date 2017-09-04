package com.phicomm.smartplug.modules.scene.addscene.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.event.DeviceListResultEvent;
import com.phicomm.smartplug.modules.scene.addscene.adapter.ControlAdapter;
import com.phicomm.smartplug.modules.scene.addscene.adapter.DeviceAdapter;
import com.phicomm.smartplug.modules.scene.addscene.adapter.OperationAdapter;
import com.phicomm.smartplug.modules.scene.addscene.contract.AddTaskContract;
import com.phicomm.smartplug.modules.scene.addscene.presenter.AddTaskPresenter;
import com.phicomm.smartplug.modules.scene.model.ControlModel;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;
import com.phicomm.smartplug.modules.scene.model.TimeModel;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.smartplug.view.CustomDialog;
import com.phicomm.widgets.PhiTitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

public class AddTaskActivity extends BaseActivity
        implements BaseQuickAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener, AddTaskContract.View {
    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.device_list)
    Gallery mDeviceList;

    @BindView(R.id.current_device_name)
    TextView currentDeviceName;

    @BindView(R.id.control_list)
    RecyclerView mControlList;

    @BindView(R.id.task_confirm_button)
    Button mTaskConfirmButton;

    Button mOperationConfirmButton;

    private DeviceAdapter mDeviceAdapter;
    private int mCurrentDeviceIndex = 0;

    private ControlAdapter mControlAdapter;
    private int mCurrentControlIndex = 0;

    private CustomDialog mDialog;
    private OperationAdapter mOperationAdapter;

    private TaskModel mTask = new TaskModel();

    // presenter
    AddTaskContract.Presenter mAddTaskPresenter;

    // time setting
    private int mCachedTimePickerHour = 0;
    private int mCachedTimePickerMinute = 0;
    private TimePicker.OnTimeChangedListener mTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            mCachedTimePickerHour = hourOfDay;
            mCachedTimePickerMinute = minute;
        }
    };

    // operation item click handler
    private BaseQuickAdapter.OnItemClickListener mOperationItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            mOperationAdapter.setCurrentSelected(position);

            mControlAdapter.getItem(mCurrentControlIndex).setCurrentOperation(mOperationAdapter.getItem(position));
            mOperationAdapter.notifyDataSetChanged();
        }
    };

    // operation confirm button handler
    private View.OnClickListener mOperationConfirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mControlAdapter.getItem(mCurrentControlIndex).getCurrentOperation() != null) {
                // set current selected control
                mControlAdapter.setCurrentSelected(mCurrentControlIndex);

                // set delay time
                TimeModel time = new TimeModel(mCachedTimePickerHour, mCachedTimePickerMinute);
                ControlModel currentControl = mControlAdapter.getItem(mCurrentControlIndex);
                currentControl.setDelayTime(time);

                // set control
                DeviceModel currentDevice = mDeviceAdapter.getModel(mCurrentDeviceIndex);
                currentDevice.setCurrentControl(currentControl);

                // set device
                mTask.setDevice(currentDevice);

                // setup confirm button
                mTaskConfirmButton.setClickable(true);
                mTaskConfirmButton.setBackgroundResource(R.drawable.confirm_button_background);
                mTaskConfirmButton.setOnClickListener(mAddTaskConfirmButtonOnClickListener);

                // update ui
                mControlAdapter.notifyDataSetChanged();
            }
            mDialog.dismiss();
        }
    };

    // add task button handler
    private View.OnClickListener mAddTaskConfirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("task", mTask);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add_task);

        initView();
        initPresenter();
        getDeviceList();
    }

    private void initView() {
        // setup title
        TitlebarUtils.initTitleBar(this, mTitleBar, R.string.add_scene);
        mTitleBar.setLeftImageResource(R.drawable.button_back);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // device list
        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceList.setAdapter(mDeviceAdapter);
        mDeviceList.setUnselectedAlpha(0.3f);
        mDeviceList.setOnItemSelectedListener(this);

        // control list
        mControlAdapter = new ControlAdapter(this);
        mControlList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mControlList.setAdapter(mControlAdapter);
        mControlAdapter.setOnItemClickListener(this);

        // confirm button
        mTaskConfirmButton.setClickable(false);
        mTaskConfirmButton.setOnClickListener(null);
        mTaskConfirmButton.setBackgroundColor(getResources().getColor(R.color.gray_color));
    }

    private void initPresenter() {
        mAddTaskPresenter = new AddTaskPresenter(this, this);
    }

    private void getDeviceList() {
        mAddTaskPresenter.getDeviceList();
    }

    @Override
    public void updateDeviceList(List<DeviceModel> list) {
        // update devices
        mDeviceAdapter.setList(list);
        if (list.size() > 1) {
            mDeviceList.setSelection(1);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == null || view == null || position < 0) {
            return;
        }

        // update device
        mCurrentDeviceIndex = position;

        // update device name
        currentDeviceName.setText(mDeviceAdapter.getModel(position).getDeviceName());

        // update control list
        mControlAdapter.setNewData(mDeviceAdapter.getModel(position).getControls());
        mControlAdapter.setCurrentSelected(-1);

        // update button
        mTaskConfirmButton.setClickable(false);
        mTaskConfirmButton.setOnClickListener(null);
        mTaskConfirmButton.setBackgroundColor(getResources().getColor(R.color.gray_color));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {
        // update control index
        mCurrentControlIndex = position;

        // reset time
        mCachedTimePickerHour = 0;
        mCachedTimePickerMinute = 0;

        // dialog
        mDialog = new CustomDialog(this, R.style.CustomDialogTheme);
        View chooseContent = LayoutInflater.from(this).inflate(R.layout.scene_add_task_set_operation_dialog, null);
        mDialog.setContentView(chooseContent);
        mDialog.setupDialog();
        mDialog.show();

        // confirm button
        mOperationConfirmButton = (Button) chooseContent.findViewById(R.id.operation_confirm_button);
        mOperationConfirmButton.setClickable(true);
        mOperationConfirmButton.setBackgroundResource(R.drawable.confirm_button_background);
        mOperationConfirmButton.setOnClickListener(mOperationConfirmButtonOnClickListener);

        // operation list
        RecyclerView operationList = (RecyclerView) chooseContent.findViewById(R.id.operation_list);
        operationList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mOperationAdapter = new OperationAdapter(this);
        mOperationAdapter.setNewData(mControlAdapter.getItem(position).getOperations());
        mOperationAdapter.setCurrentSelected(0);
        mOperationAdapter.setOnItemClickListener(mOperationItemClickListener);
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerDecoration.setDrawable(getResources().getDrawable(R.drawable.operation_list_divider));
        operationList.setAdapter(mOperationAdapter);
        operationList.addItemDecoration(dividerDecoration);

        // set current operation
        mControlAdapter.getItem(mCurrentControlIndex).setCurrentOperation(mOperationAdapter.getItem(mOperationAdapter.getCurrentSelected()));

        // time picker
        TimePicker tp = (TimePicker) chooseContent.findViewById(R.id.time_picker);
        tp.setIs24HourView(true);
        tp.setCurrentHour(0);
        tp.setCurrentMinute(0);
        tp.setOnTimeChangedListener(mTimeChangedListener);
    }

    @Override
    public Object getRxLifeCycleObj() {
        return this;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDeviceList(DeviceListResultEvent event) {
        if (event.error.equals("113") || event.error.equals("100")) {
            showToast(R.string.empty_layout_tip1);
        }
    }
}
