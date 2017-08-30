package com.moscase.shouhuan.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.idtk.smallchart.data.BarData;
import com.idtk.smallchart.interfaces.iData.IBarData;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.BarChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;



public class CalendarActivity extends Activity {

    private DatePicker mDatePicker;
    private String[] mDate;
    private BarChartView mBarChartView;
    private ArrayList<IBarData> mDataList = new ArrayList<>();
    private BarData mBarData = new BarData();
    private ArrayList<PointF> mPointArrayList3 = new ArrayList<>();
    private List<Float> mFloats = new ArrayList<>();

    private int mCurrentMonth;
    private int mCurrentYear;

    protected float[][] points = new float[][]{{1, 1}, {2, 2}, {3, 3500}, {4, 4900}, {5, 5600},
            {6, 5600}, {7, 7},
            {8, 8}, {9, 9}, {10, 10}, {11, 11}, {12, 12}, {13, 13}, {14, 14}, {15, 15}, {16, 16},
            {17, 17},
            {18, 18}, {19, 19}, {20, 20}, {21, 21}, {22, 22}, {23, 23}, {24, 24}, {25, 25}, {26,
            26}, {27, 27},
            {28, 28}, {29, 29}, {30, 30}, {31, 10000}};

    private float[] pointBar = new float[]{25000,2500,2500,2500,2500,2500,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000
    ,50000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,104000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mDatePicker = (DatePicker) findViewById(R.id.datepicker);
        mBarChartView = (BarChartView) findViewById(R.id.barChartView);
        mBarChartView.setBarChartList(pointBar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentMonth = mDatePicker.getMonth();
                mCurrentYear = mDatePicker.getYear();
                Toast.makeText(CalendarActivity.this, ""+mCurrentYear+mCurrentMonth, Toast.LENGTH_SHORT).show();
            }
        }, 100);
//        mBarChart = (BarChart) findViewById(R.id.combineChart);
//
////        是否开启动画
////        mBarChart.isAnimated = false;
//        mBarChart.setBarWidth(5);
//        mBarChart.setAxisTextSize(pxTodp(13));
//
//        mBarData.setColor(Color.RED);
////        mBarData.setPaintWidth(5);
////        是否显示Y轴的值
////        mBarData.setIsTextSize(false);
//        mBarData.setTextSize(pxTodp(6));
//        points[0][1] = 1400;
//        for (int i =0; i<31;i++){
//            mPointArrayList3.add(new PointF(points[i][0], points[i][1]));
//        }
//        mBarData.setValue(mPointArrayList3);
//        mDataList.add(mBarData);
//        mBarChart.setDataList(mDataList);
//        mBarChart.computeYAxis();

        //画右上角的点

        List<String> tmpTR = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            tmpTR.add("2017-8-" + i);
        }
        DPCManager.getInstance().setDecorTR(tmpTR);
        mDatePicker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.RED);
                canvas.drawCircle(rect.centerX() + 10, rect.centerY() - 10, rect.width() / 5,
                        paint);
            }
        });


        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-M-d");
        String date = sDateFormat.format(new java.util.Date());
        mDate = date.split("-");
        mDatePicker.setMode(DPMode.SINGLE);
        mDatePicker.setDate(Integer.parseInt(mDate[0]), Integer.parseInt(mDate[1]));

        mDatePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                Toast.makeText(CalendarActivity.this, date, Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected float pxTodp(float value) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float valueDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
        return valueDP;
    }

}
