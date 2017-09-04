package com.phicomm.smartplug.modules.device.devicedetails.fragment;

import android.graphics.Color;

import com.phicomm.smartplug.modules.data.remote.beans.device.PowerStaticsBean;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by feilong.yang on 2017/7/17.
 */

public class ColumnChartManager {

    public List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    public List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();

    private final float MIN_Y = 0f;//Y轴坐标最小值
    private final float MAX_Y = 5f;//Y轴坐标最大值
    public static final int COLOR = 0xFFF5AD47;
    private boolean isHasColumnLabels = false;            //是否显示列标签
    private boolean isColumnsHasSelected = true;          //设置列点击后效果(消失/显示标签)
    private boolean isHasAxes = true;                     //是否显示坐标轴
    private boolean isZoomEnabled = false;                //是否支持缩放

    private float mMaxValues = 0f;

    public static ColumnChartManager mInstance;


    public static ColumnChartManager getInstance() {
        if (null == mInstance) {
            mInstance = new ColumnChartManager();
        }
        return mInstance;
    }

    private void initData(ArrayList<PowerStaticsBean> monthList) {
        if (null == monthList) {
            return;
        }
        clearData();
        getAxisXLables(monthList);
//        getAxisYLables();
    }

    private void clearData() {
        mMaxValues = 0f;
        mAxisXValues.clear();
//        mAxisYValues.clear();
    }

    public void initColumnChart(ArrayList<PowerStaticsBean> monthList, ColumnChartView mColumnCharView) {

        initData(monthList);

        List<Column> columns = new ArrayList<>();
        drawColumn(monthList, columns);
        ColumnChartData mColumnChartData = new ColumnChartData(columns);
        //设置是否堆叠
        mColumnChartData.setStacked(false);
        //设置柱形宽度
        mColumnChartData.setFillRatio(0.4f);
        setColumnChartStytle(mColumnChartData, mColumnCharView);
    }

    private void drawColumn(ArrayList<PowerStaticsBean> monthList, List<Column> columns) {
        if (null == monthList)
            return;

        ColumnChartValueFormatter formatter = new SimpleColumnChartValueFormatter(2);
        List<SubcolumnValue> values;
        for (int i = 0; i < monthList.size(); ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(getTotalPowerData(monthList.get(i)), COLOR));
            Column column = new Column(values);
            column.setHasLabels(isHasColumnLabels);
            column.setHasLabelsOnlyForSelected(isColumnsHasSelected);
            column.setFormatter(formatter);
            columns.add(column);
        }
    }

    private void setColumnChartStytle(ColumnChartData mColumnChartData, ColumnChartView mColumnCharView) {
        if (isHasAxes) {
            Axis axisX = new Axis(mAxisXValues); //X轴
            axisX.setTextColor(Color.BLACK);
            axisX.setHasLines(true);
            axisX.setTextSize(10);
            mColumnChartData.setAxisXBottom(axisX);

            Axis axisY = new Axis(); //y轴
            axisY.setTextColor(Color.BLACK);
            axisY.setHasLines(true);
            axisY.setTextSize(10);
            //axisY.setValues(mAxisYValues);
            mColumnChartData.setAxisYLeft(axisY);
        } else {
            mColumnChartData.setAxisXBottom(null);
            mColumnChartData.setAxisYLeft(null);
        }
        mColumnCharView.setValueSelectionEnabled(isColumnsHasSelected);
        mColumnCharView.setZoomEnabled(isZoomEnabled);//设置是否支持缩放
        mColumnCharView.setColumnChartData(mColumnChartData);
        Viewport v = new Viewport(mColumnCharView.getMaximumViewport());
        v.bottom = MIN_Y;
        v.top = getAxisYMaxLable();
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定
        mColumnCharView.setMaximumViewport(v);
//      v.left = mAxisXValues.size() - 7;
//      v.right = mAxisXValues.size() - 1;
        mColumnCharView.setCurrentViewport(v);
    }


    private float getTotalPowerData(PowerStaticsBean bean) {
        float value = bean.day + bean.night;
        if (value > mMaxValues)
            mMaxValues = value;
        return value;
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables(ArrayList<PowerStaticsBean> monthList) {
        for (int i = 0; i < monthList.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(getSubString(monthList.get(i).oneMonth)));
        }
    }

    /**
     * 设置y轴的显示
     */
//    private void getAxisYLables() {
//        for (float i = MIN_Y; i <= MAX_Y; i++) {
//            mAxisYValues.add(new AxisValue(i).setLabel((int) i + ""));
//        }
//    }

    /**
     * 获取y轴最大显示值
     */
    private float getAxisYMaxLable() {
        if (mMaxValues > 0) {
            float temp = Math.round(mMaxValues);
            if (mMaxValues > temp) {
                temp += 1;
            }
            return temp + 1;
        }
        //为0的情况，给一个默认值
        return MAX_Y;
    }

    private static String getSubString(String string) {
        String[] strings = string.split("-");
        int month = Integer.parseInt(strings[1]);
        return month + "";
    }

}
