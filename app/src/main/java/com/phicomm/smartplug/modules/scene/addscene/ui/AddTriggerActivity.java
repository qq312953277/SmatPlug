package com.phicomm.smartplug.modules.scene.addscene.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.base.BaseActivity;
import com.phicomm.smartplug.modules.scene.addscene.adapter.TriggerAdapter;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;
import com.phicomm.smartplug.utils.TitlebarUtils;
import com.phicomm.widgets.PhiTitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class AddTriggerActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.title_bar)
    PhiTitleBar mTitleBar;

    @BindView(R.id.trigger_list)
    Gallery mTriggers;

    @BindView(R.id.manual_trigger)
    LinearLayout mManuallyTrigger;

    @BindView(R.id.timer_trigger)
    LinearLayout mTimerTrigger;

    @BindView(R.id.timer_repeat_date_7)
    Button mTimerRepeatDate7;

    @BindView(R.id.timer_repeat_date_1)
    Button mTimerRepeatDate1;

    @BindView(R.id.timer_repeat_date_2)
    Button mTimerRepeatDate2;

    @BindView(R.id.timer_repeat_date_3)
    Button mTimerRepeatDate3;

    @BindView(R.id.timer_repeat_date_4)
    Button mTimerRepeatDate4;

    @BindView(R.id.timer_repeat_date_5)
    Button mTimerRepeatDate5;

    @BindView(R.id.timer_repeat_date_6)
    Button mTimerRepeatDate6;

    @BindView(R.id.timer_trigger_time_picker)
    TimePicker mTimePicker;

    @BindView(R.id.trigger_confirm_button)
    Button mTriggerConfirmButton;

    private TriggerAdapter mTriggerAdapter;

    private TriggerModel mTrigger;

    // cache relationship between button and weekday
    private HashMap<Button, Integer> mButtonIntegerHashMap = new HashMap<>();
    private HashMap<Integer, Button> mIntegerButtonHashMap = new HashMap<>();

    private View.OnClickListener mTimerRepeatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                if (mTrigger.getTriggerTime().getRepeatDays().contains(mButtonIntegerHashMap.get(v))) {
                    v.setBackgroundResource(R.drawable.timer_repeat_bg);
                    mTrigger.getTriggerTime().getRepeatDays().remove(mButtonIntegerHashMap.get(v));
                } else {
                    v.setBackgroundResource(R.drawable.timer_repeat_bg_pressed);
                    mTrigger.getTriggerTime().getRepeatDays().add(mButtonIntegerHashMap.get(v));
                }
            }
        }
    };

    private TimePicker.OnTimeChangedListener mTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            mTrigger.getTriggerTime().setHour(hourOfDay);
            mTrigger.getTriggerTime().setMinute(minute);
        }
    };

    private View.OnClickListener mConfirmConditonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // sort repeat days list
            Collections.sort(mTrigger.getTriggerTime().getRepeatDays());

            // pass result
            Intent intent = new Intent();
            intent.putExtra("trigger", mTrigger);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trigger);

        mTrigger = (TriggerModel) getIntent().getSerializableExtra("trigger");
        if (mTrigger == null) {
            mTrigger = new TriggerModel();
        }

        initView();
    }

    private void initView() {
        // init title
        TitlebarUtils.initTitleBar(this, mTitleBar, R.string.set_condition);
        mTitleBar.setLeftImageResource(R.drawable.button_back);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // init condition list
        mTriggerAdapter = new TriggerAdapter(this);
        mTriggerAdapter.setList(initTriggers());
        mTriggers.setAdapter(mTriggerAdapter);
        mTriggers.setUnselectedAlpha(0.3f);
        mTriggers.setOnItemSelectedListener(this);
        mTriggers.setSelection(mTrigger.getTriggerType());

        // init repeat
        mButtonIntegerHashMap.put(mTimerRepeatDate1, 1);
        mIntegerButtonHashMap.put(1, mTimerRepeatDate1);
        mTimerRepeatDate1.setText(getResources().getStringArray(R.array.week)[0]);
        mTimerRepeatDate1.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate2, 2);
        mIntegerButtonHashMap.put(2, mTimerRepeatDate2);
        mTimerRepeatDate2.setText(getResources().getStringArray(R.array.week)[1]);
        mTimerRepeatDate2.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate3, 3);
        mIntegerButtonHashMap.put(3, mTimerRepeatDate3);
        mTimerRepeatDate3.setText(getResources().getStringArray(R.array.week)[2]);
        mTimerRepeatDate3.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate4, 4);
        mIntegerButtonHashMap.put(4, mTimerRepeatDate4);
        mTimerRepeatDate4.setText(getResources().getStringArray(R.array.week)[3]);
        mTimerRepeatDate4.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate5, 5);
        mIntegerButtonHashMap.put(5, mTimerRepeatDate5);
        mTimerRepeatDate5.setText(getResources().getStringArray(R.array.week)[4]);
        mTimerRepeatDate5.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate6, 6);
        mIntegerButtonHashMap.put(6, mTimerRepeatDate6);
        mTimerRepeatDate6.setText(getResources().getStringArray(R.array.week)[5]);
        mTimerRepeatDate6.setOnClickListener(mTimerRepeatClickListener);

        mButtonIntegerHashMap.put(mTimerRepeatDate7, 7);
        mIntegerButtonHashMap.put(7, mTimerRepeatDate7);
        mTimerRepeatDate7.setText(getResources().getStringArray(R.array.week)[6]);
        mTimerRepeatDate7.setOnClickListener(mTimerRepeatClickListener);

        // set checked days
        for (Integer i : mTrigger.getTriggerTime().getRepeatDays()) {
            mIntegerButtonHashMap.get(i).setBackgroundResource(R.drawable.timer_repeat_bg_pressed);
        }

        // time picker
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(mTrigger.getTriggerTime().getHour());
        mTimePicker.setCurrentMinute(mTrigger.getTriggerTime().getMinute());
        mTimePicker.setOnTimeChangedListener(mTimeChangedListener);

        // init button sure
        mTriggerConfirmButton.setOnClickListener(mConfirmConditonClickListener);
    }

    private List<TriggerModel> initTriggers() {
        List<TriggerModel> triggers = new ArrayList<>();

        // manually execute
        TriggerModel trigger = new TriggerModel();
        trigger.setTriggerType(TriggerModel.TRIGGER_MANUALLY);
        trigger.setImageId(R.drawable.scene_trigger_manually);
        trigger.setTriggerDescription(R.string.trigger_manually);
        triggers.add(trigger);

        // timer execute
        trigger = new TriggerModel();
        trigger.setTriggerType(TriggerModel.TRIGGER_TIMER);
        trigger.setImageId(R.drawable.scene_timer_trigger_icon);
        trigger.setTriggerDescription(R.string.trigger_timer);
        triggers.add(trigger);

        return triggers;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == null || view == null || position < 0) {
            return;
        }

        mTrigger.setImageId(mTriggerAdapter.getModel(position).getImageId());
        mTrigger.setTriggerType(mTriggerAdapter.getModel(position).getTriggerType());
        mTrigger.setTriggerDescription(mTriggerAdapter.getModel(position).getTriggerDescription());

        if (position == 0) {
            mManuallyTrigger.setVisibility(View.VISIBLE);
            mTimerTrigger.setVisibility(View.INVISIBLE);
        } else {
            mManuallyTrigger.setVisibility(View.INVISIBLE);
            mTimerTrigger.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
