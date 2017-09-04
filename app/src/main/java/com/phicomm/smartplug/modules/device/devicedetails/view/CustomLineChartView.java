package com.phicomm.smartplug.modules.device.devicedetails.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Field;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.renderer.LineChartRenderer;
import lecho.lib.hellocharts.view.AbstractChartView;

/**
 * Created by feilong.yang on 2017/8/21.
 */

public class CustomLineChartView extends AbstractChartView implements LineChartDataProvider {

    private static final String TAG = "CustomLineChartView";
    protected LineChartData data;
    protected LineChartOnValueSelectListener onValueTouchListener = new DummyLineChartOnValueSelectListener();

    public CustomLineChartView(Context context) {
        this(context, null, 0);
    }

    public CustomLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LineChartRenderer lineChartRenderer = new LineChartRenderer(context, this, this);
        setChartRenderer(lineChartRenderer);
        setLineChartData(LineChartData.generateDummyData());
        try {
            //为了修改图表中节点点击后高亮的 pointradius
            Field field = lineChartRenderer.getClass().getDeclaredField("touchToleranceMargin");
            field.setAccessible(true);
            int touchToleranceMargin = (int) field.get(lineChartRenderer);
            field.set(lineChartRenderer, touchToleranceMargin / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LineChartData getLineChartData() {
        return data;
    }

    @Override
    public void setLineChartData(LineChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for LineChartView");
        }

        if (null == data) {
            this.data = LineChartData.generateDummyData();
        } else {
            this.data = data;
        }

        super.onChartDataChange();
    }

    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            PointValue point = data.getLines().get(selectedValue.getFirstIndex()).getValues()
                    .get(selectedValue.getSecondIndex());
            onValueTouchListener.onValueSelected(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(), point);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public LineChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(LineChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }
}
