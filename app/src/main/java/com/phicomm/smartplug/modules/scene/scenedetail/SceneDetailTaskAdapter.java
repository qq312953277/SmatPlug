package com.phicomm.smartplug.modules.scene.scenedetail;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.scene.model.ControlModel;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;
import com.phicomm.smartplug.modules.scene.model.TimeModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.modules.scene.utils.SceneResultCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SceneDetailTaskAdapter extends BaseQuickAdapter<TaskModel, BaseViewHolder> {
    private Context mContext;
    private boolean editMode;
    protected SceneModel scene;
    private Date currentServerDate;
    private boolean someTaskDeleted = false;

    private static final int TASK_NOT_STARTED = -1;
    private static final int TASK_CANCELED = -2;
    private static final int BLANK = -3;
    private static final int SCENE_NEVER_STARTED = -4;

    private static final int TASK_STATE_NOT_EXECUTE = 0;
    private static final int TASK_STATE_EXECUTED = 1;

    public SceneDetailTaskAdapter(Context context) {
        super(R.layout.item_scene_detail_task);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskModel item) {
        DeviceModel device = item.getDevice();
        ControlModel control = device.getCurrentControl();
        int hour = control.getDelayTime().getHour();
        int minute = control.getDelayTime().getMinute();
        int result = 0;
        int currState = 0;

        try {
            result = Integer.parseInt(item.getResultCode());
            currState = Integer.parseInt(item.getCurrentstate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get time delay prompt string
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getResources().getString(R.string.delay_time_label));
        if (hour > 0) {
            sb.append(String.format(mContext.getResources().getString(R.string.n_hour), hour));
        }
        if (minute > 0) {
            sb.append(String.format(mContext.getResources().getString(R.string.n_minute), minute));
        }

        // init view
        helper.setImageResource(R.id.device_icon, device.getImageId())
                .setText(R.id.device_name, device.getDeviceName())
                .setText(R.id.control_name, control.getControlName())
                .setText(R.id.operation, control.getCurrentOperation().getOperationName())
                .setText(R.id.time_delay, sb)
                .setVisible(R.id.time_delay, !(hour < 0 || minute < 0 || (hour == 0 && minute == 0)))
                .setOnClickListener(R.id.delete_task, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        someTaskDeleted = true;

                        remove(helper.getLayoutPosition());
                        notifyDataSetChanged();
                    }
                }).setVisible(R.id.delete_task, editMode);

        TextView lastTaskExecuteResult = helper.getView(R.id.last_task_execute_result);
        TextView currentTaskStatus = helper.getView(R.id.current_task_status);
        if (editMode) {
            lastTaskExecuteResult.setVisibility(View.GONE);
            currentTaskStatus.setVisibility(View.GONE);
        } else {
            // task execute status
            lastTaskExecuteResult.setVisibility(View.VISIBLE);
            currentTaskStatus.setVisibility(View.VISIBLE);

            if (scene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER && scene.getTrigger().getTriggerTime().getRepeatDays().size() > 0) {
                if (result == BLANK) {
                    lastTaskExecuteResult.setText(mContext.getResources().getString(SceneResultCode.getResultString(result)));
                } else {
                    lastTaskExecuteResult.setText(mContext.getResources().getString(SceneResultCode.getResultString(result)) + " " + getTimeString(item.getLastExecuteTime()));
                }

                if (scene.getStatus()) {
                    if (currState == TASK_STATE_NOT_EXECUTE && scene.isRunning()) {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(TASK_NOT_STARTED)));
                    } else {
                        currentTaskStatus.setText(mContext.getResources().getString(R.string.next_execute_time_prefix)
                                + " " + getNextExecuteTime(mContext, scene.getTrigger().getTriggerTime(), item.getDevice().getCurrentControl().getDelayTime()));
                    }
                } else {
                    if (result == BLANK) {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(SCENE_NEVER_STARTED)));
                    } else {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(BLANK)));
                    }
                }
            } else {
                if (result == BLANK) {
                    lastTaskExecuteResult.setText(mContext.getResources().getString(SceneResultCode.getResultString(result)));
                } else {
                    lastTaskExecuteResult.setText(mContext.getResources().getString(SceneResultCode.getResultString(result)) + " " + getTimeString(item.getLastExecuteTime()));
                }

                if (scene.getStatus()) {
                    if (currState == TASK_STATE_NOT_EXECUTE) {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(TASK_NOT_STARTED)));
                    } else {
                        currentTaskStatus.setText(SceneResultCode.getResultString(BLANK));
                    }
                } else {
                    if (result == BLANK) {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(SCENE_NEVER_STARTED)));
                    } else {
                        currentTaskStatus.setText(mContext.getResources().getString(SceneResultCode.getResultString(BLANK)));
                    }
                }
            }
        }
    }

    private String getTimeString(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            return sdf.format(new Date(time * 1000L));
        } catch (Exception e) {
            return "";
        }
    }

    private String getNextExecuteTime(Context context, TimeModel triggerTime, TimeModel delayTime) {
        if (triggerTime == null
                || delayTime == null
                || triggerTime.getRepeatDays() == null
                || triggerTime.getRepeatDays().size() == 0
                || currentServerDate == null) {
            return null;
        }

        boolean foundNext = false;
        boolean isNextWeek = false;
        Date nextExecuteDate = null;
        Date sampleDate = null;
        try {
            sampleDate = new SimpleDateFormat("yy-MM-dd hh:mm").parse("2017-07-03 00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // get current week day and time
        Calendar curr = Calendar.getInstance();
        int currDay = currentServerDate.getDay() == 0 ? 7 : currentServerDate.getDay();
        int currHour = currentServerDate.getHours();
        int currMinute = currentServerDate.getMinutes();
        curr.setTime(sampleDate);
        curr.add(Calendar.MINUTE, currMinute);
        curr.add(Calendar.HOUR_OF_DAY, currHour);
        curr.add(Calendar.DAY_OF_WEEK, currDay - 1);
        Date currDate = curr.getTime();

        // check date
        Calendar cal = Calendar.getInstance();
        cal.setTime(sampleDate);
        cal.add(Calendar.HOUR_OF_DAY, triggerTime.getHour());
        cal.add(Calendar.MINUTE, triggerTime.getMinute());
        cal.add(Calendar.HOUR_OF_DAY, delayTime.getHour());
        cal.add(Calendar.MINUTE, delayTime.getMinute());
        List<Date> executeDateList = new ArrayList<>();
        int formerDay = 1;
        for (Integer repeatDay : triggerTime.getRepeatDays()) {
            cal.add(Calendar.DAY_OF_WEEK, repeatDay - formerDay);
            executeDateList.add(cal.getTime());
            formerDay = repeatDay;

            if (currDate.getTime() < cal.getTime().getTime()) {
                // get trigger time
                Calendar repeatDayTriggerTime = Calendar.getInstance();
                repeatDayTriggerTime.setTime(cal.getTime());
                repeatDayTriggerTime.add(Calendar.HOUR_OF_DAY, 0 - delayTime.getHour());
                repeatDayTriggerTime.add(Calendar.MINUTE, 0 - delayTime.getMinute());

                // check time
                if (((currDate.getTime() > repeatDayTriggerTime.getTime().getTime()) && scene.isRunning())
                        || (currDate.getTime() < repeatDayTriggerTime.getTime().getTime())) {
                    nextExecuteDate = cal.getTime();
                    foundNext = true;
                    break;
                }
            }
        }

        if (!foundNext) {
            nextExecuteDate = executeDateList.get(0);
            isNextWeek = true;
        }

        // found next day
        int nextExecuteDay = nextExecuteDate.getDay() == 0 ? 7 : nextExecuteDate.getDay();
        int nextExecuteHour = nextExecuteDate.getHours();
        int nextExecuteMinute = nextExecuteDate.getMinutes();

        String weekDay;
        switch (nextExecuteDay) {
            case 1:
                weekDay = context.getResources().getString(R.string.monday);
                break;
            case 2:
                weekDay = context.getResources().getString(R.string.tuesday);
                break;
            case 3:
                weekDay = context.getResources().getString(R.string.wednesday);
                break;
            case 4:
                weekDay = context.getResources().getString(R.string.thursday);
                break;
            case 5:
                weekDay = context.getResources().getString(R.string.friday);
                break;
            case 6:
                weekDay = context.getResources().getString(R.string.saturday);
                break;
            case 7:
                weekDay = context.getResources().getString(R.string.sunday);
                break;
            default:
                weekDay = "";
                break;
        }

        return (isNextWeek ? context.getResources().getString(R.string.next_week) : "") + weekDay
                + " "
                + ((nextExecuteHour >= 10) ? ("" + nextExecuteHour) : ("0" + nextExecuteHour)) + ":"
                + ((nextExecuteMinute >= 10) ? ("" + nextExecuteMinute) : ("0" + nextExecuteMinute));
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public SceneModel getScene() {
        return scene;
    }

    public void setScene(SceneModel scene) {
        this.scene = scene;
    }

    public Date getCurrentServerDate() {
        return currentServerDate;
    }

    public void setCurrentServerDate(Date currentServerDate) {
        this.currentServerDate = currentServerDate;
    }

    public boolean isSomeTaskDeleted() {
        return someTaskDeleted;
    }

    public void setSomeTaskDeleted(boolean someTaskDeleted) {
        this.someTaskDeleted = someTaskDeleted;
    }
}
