package com.phicomm.smartplug.modules.device.devicedetails.fragment;

import android.graphics.Color;

import com.phicomm.smartplug.modules.data.remote.beans.device.PowerStaticsBean;
import com.phicomm.smartplug.modules.device.devicedetails.view.CustomLineChartView;
import com.phicomm.smartplug.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;

/**
 * Created by feilong.yang on 2017/7/17.
 */

public class LineChartManager {

    public static LineChartManager mInstance;
    public List<PointValue> mPointDayValues = new ArrayList<PointValue>();
    public List<PointValue> mPointNightValues = new ArrayList<PointValue>();
    public List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    public List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
    private final float MIN_Y = 0f;//Y轴坐标最小值
    private final float MAX_Y = 5f;//Y轴坐标最大值
    public static final int RED_COLOR = 0XFFFF0000;
    public static final int GREEN_COLOR = 0XFF009944;

    private boolean isHasLines = true;                 //是否显示折线/曲线
    private boolean isFilled = false;                  //是否填充线下方区域
    private boolean isCubic = false;                   //是否是立体的
    private boolean isHasPoints = true;                //是否显示线上的节点
    private boolean isHasPointsLabels = false;         //是否显示节点上的标签信息
    private boolean isPointsHasSelected = true;        //设置节点点击后效果(消失/显示标签)
    private boolean isZoomEnabled = false;             //是否支持缩放
    private ValueShape pointsShape = ValueShape.CIRCLE;//点的形状(圆/方/菱形)

    private float mDayMaxValues = 0f;
    private float mNightMaxValues = 0f;


    public static LineChartManager getInstance() {
        if (null == mInstance) {
            mInstance = new LineChartManager();
        }
        return mInstance;
    }

    private void initData(ArrayList<PowerStaticsBean> dayList) {
        if (null == dayList) {
            return;
        }
        clearData();
        if (dayList.size() == 1) {
            PowerStaticsBean bean = new PowerStaticsBean();
            bean.oneDay = "0";
            bean.day = 0;
            bean.night = 0;
            dayList.add(0, bean);
        }
        getAxisXLables(dayList);
//        getAxisYLables();
        getDayPoints(dayList);
        getNightPoints(dayList);
    }

    private void clearData() {
        mDayMaxValues = 0f;
        mNightMaxValues = 0f;
        mPointDayValues.clear();
        mPointNightValues.clear();
        mAxisXValues.clear();
//        mAxisYValues.clear();
    }

    /**
     * 本月每天数据
     *
     * @param dayList
     */
    public void initLineChart(ArrayList<PowerStaticsBean> dayList, CustomLineChartView lineChartView) {
        initData(dayList);
        List<Line> lines = new ArrayList<Line>();
        drawLine(lines);
        LineChartData data = new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);           //设置基准数(大概是数据范围)
        drawAxisX(data);
        drawAxisY(data);
        setLineChartStytle(data, lineChartView);
    }

    private void drawLine(List<Line> lines) {
        LineChartValueFormatter formatter = new SimpleLineChartValueFormatter(2);
        Line line = new Line(mPointNightValues).setColor(GREEN_COLOR);
        line.setShape(pointsShape);                 //设置点的形状
        line.setHasLines(isHasLines);               //设置是否显示线
        line.setHasPoints(isHasPoints);             //设置是否显示节点
        line.setStrokeWidth(1);                     //线条的粗细
        line.setCubic(isCubic);                     //设置线是否立体或其他效果
        line.setFilled(isFilled);                   //设置是否填充线下方区域
        line.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
        line.setHasLabelsOnlyForSelected(isPointsHasSelected); //设置节点点击的效果
        line.setPointRadius(4);
        line.setFormatter(formatter);//显示小数点
        lines.add(line);

        Line line2 = new Line(mPointDayValues).setColor(RED_COLOR);
        line2.setShape(pointsShape);                 //设置点的形状
        line2.setHasLines(isHasLines);               //设置是否显示线
        line2.setHasPoints(isHasPoints);             //设置是否显示节点
        line2.setStrokeWidth(1);                     //线条的粗细
        line2.setCubic(isCubic);                     //设置线是否立体或其他效果
        line2.setFilled(isFilled);                   //设置是否填充线下方区域
        line2.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
        line2.setPointRadius(4);
        line2.setHasLabelsOnlyForSelected(isPointsHasSelected); //设置节点点击的效果
        line2.setFormatter(formatter);//显示小数点
        lines.add(line2);

    }

    private void drawAxisX(LineChartData data) {
        //坐标轴X
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);
        axisX.setHasLines(true);
        axisX.setValues(mAxisXValues);
        axisX.setTextSize(10);
        data.setAxisXBottom(axisX);//x 轴在底部
    }

    public void drawAxisY(LineChartData data) {
        Axis axisY = new Axis();
        axisY.setTextColor(Color.BLACK);
        axisY.setHasLines(true);
        axisY.setTextSize(10);
        // axisY.setValues(mAxisYValues);
        data.setAxisYLeft(axisY);
    }

    private void setLineChartStytle(LineChartData data, CustomLineChartView lineChartView) {
        lineChartView.setLineChartData(data);
        lineChartView.setZoomEnabled(isZoomEnabled);//设置是否支持缩放
        lineChartView.setValueSelectionEnabled(isPointsHasSelected);
        //lineChartView.setValueSelectionEnabled(isPointsHasSelected);
        Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = MIN_Y;
        v.top = getAxisYMaxLable();
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定
        lineChartView.setMaximumViewport(v);

        v.left = mAxisXValues.size() - 7;
        v.right = mAxisXValues.size() - 1;
        lineChartView.setCurrentViewport(v);
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables(ArrayList<PowerStaticsBean> dayList) {
        for (int i = 0; i < dayList.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(getSubString(dayList.get(i).oneDay)));
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
        if (mDayMaxValues > 0 || mNightMaxValues > 0) {
            float temp = Math.max(mDayMaxValues, mNightMaxValues);
            if (temp > Math.round(temp)) {
                temp = Math.round(temp) + 1;
            } else {
                temp = Math.round(temp);
            }
            return temp + 1;
        }
        //都为0的情况，给一个默认值
        return MAX_Y;
    }

    /**
     * 图表的每个点的显示
     */
    public void getDayPoints(ArrayList<PowerStaticsBean> dayList) {
        for (int i = 0; i < dayList.size(); i++) {
            float dayValue = dayList.get(i).day;
            if (dayValue > mDayMaxValues) {
                mDayMaxValues = dayValue;
            }
            mPointDayValues.add(new PointValue(i, dayValue));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getNightPoints(ArrayList<PowerStaticsBean> dayList) {
        for (int i = 0; i < dayList.size(); i++) {
            float nightValue = dayList.get(i).night;
            if (nightValue > mNightMaxValues) {
                mNightMaxValues = nightValue;
            }
            mPointNightValues.add(new PointValue(i, nightValue));
        }
    }

    private String getSubString(String string) {
        if (StringUtils.isNull(string) || string.equals("0")) {
            return string;
        }
        String[] strings = string.split("-");
        int month = Integer.parseInt(strings[1]);
        int day = Integer.parseInt(strings[2]);
        return month + "-" + day;
    }

}
