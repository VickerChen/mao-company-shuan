package com.moscase.shouhuan.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idtk.smallchart.data.BarData;
import com.idtk.smallchart.interfaces.iData.IBarData;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.BushuData;
import com.moscase.shouhuan.view.BarChartView;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import me.drakeet.materialdialog.MaterialDialog;


public class CalendarActivity extends Activity {

    private DatePicker mDatePicker;
    private String[] mDate;
    private BarChartView mBarChartView;
    private ArrayList<IBarData> mDataList = new ArrayList<>();
    private BarData mBarData = new BarData();
    private ArrayList<PointF> mPointArrayList3 = new ArrayList<>();
    private List<Float> mFloats = new ArrayList<>();
    private ImageView mBack;

    private int mCurrentMonth;
    private int mCurrentYear;

    private TextView mDateToShow;
    private TextView mSportTime;
    private TextView mTotalbushu;
    private TextView mTotalkaluli;
    private TextView mTotalDistance;
    private ImageView mCancel;

    private SharedPreferences mSharedPreferences;

//    protected float[][] points = new float[][]{{1, 1}, {2, 2}, {3, 3500}, {4, 4900}, {5, 5600},
//            {6, 5600}, {7, 7},
//            {8, 8}, {9, 9}, {10, 10}, {11, 11}, {12, 12}, {13, 13}, {14, 14}, {15, 15}, {16, 16},
//            {17, 17},
//            {18, 18}, {19, 19}, {20, 20}, {21, 21}, {22, 22}, {23, 23}, {24, 24}, {25, 25}, {26,
//            26}, {27, 27},
//            {28, 28}, {29, 29}, {30, 30}, {31, 10000}};

    private float[] pointBar = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
            , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.chenhang.month");
        filter.addAction("com.chenhang.year");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);


        mSharedPreferences = getSharedPreferences("myInfo", MODE_PRIVATE);

        mDatePicker = (DatePicker) findViewById(R.id.datepicker);
        mBarChartView = (BarChartView) findViewById(R.id.barChartView);
        mBarChartView.setYmax(mSharedPreferences.getInt("mubiao", 10000));
        mBarChartView.setBarChartList(pointBar);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentMonth = mDatePicker.getMonth();
                mCurrentYear = mDatePicker.getYear();

            }
        }, 500);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /**
         * 我不知道为什么，本来画红点是能画的，但是无论是在广播里还是在哪儿，都画不了，我只能在onCreate里画，我也懒得研究
         * 所以本来主页面存储日期的格式是 20171024的，我不得已给换成2017-10-24了，因为画点的时候只能用后面这个格式，
         * 所以我本来查数据库的时候挺方便的，现在还要把"-"replace成""了
         */
        List<BushuData> bushuDatas = DataSupport.findAll(BushuData.class);
        List<String> mTmpTR = new ArrayList<>();
        for (BushuData bushuData : bushuDatas) {
            if (bushuData.getBushu() != 0) {
                mTmpTR.add(bushuData.getRiqi());
                Log.d("koma红点日期是", bushuData.getRiqi());
            }
        }

        //画右上角的红点
        DPCManager.getInstance().setDecorTR(mTmpTR);
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

                List<BushuData> bushuData = DataSupport.where("riqi = ?", date).find(BushuData
                        .class);
                if (bushuData == null || bushuData.size() == 0) {

                } else {
                    View view = LayoutInflater.from(CalendarActivity.this).inflate(R.layout
                            .detail_dialog, null, false);
                    mDateToShow = view.findViewById(R.id.date);
                    mSportTime = view.findViewById(R.id.sporttime);
                    mTotalbushu = view.findViewById(R.id.totalbushu);

                    mTotalDistance = view.findViewById(R.id.totaldistance);
                    mTotalkaluli = view.findViewById(R.id.totalkaluli);
                    mCancel = view.findViewById(R.id.cancel);
                    mDateToShow.setText(date);
                    mTotalbushu.setText(bushuData.get(0).getBushu() + "步");
                    String time = bushuData.get(0).getSporttime();
                    String h = time.substring(0, 2);
                    String m = time.substring(2, 4);
                    String s = time.substring(4, 6);
                    if (Integer.valueOf(h) == 0) {
                        if (Integer.valueOf(m) == 0) {
                            mSportTime.setText(Integer.valueOf(s) + "秒");
                        } else {
                            mSportTime.setText(Integer.valueOf(m) + "分" + Integer.valueOf(s) + "秒");
                        }
                    } else {
                        mSportTime.setText(Integer.valueOf(h) + "时" + Integer.valueOf(m) + "分" +
                                Integer.valueOf(s) + "秒");
                    }

                    mTotalkaluli.setText(bushuData.get(0).getKaluli() + "千卡");
                    mTotalDistance.setText(bushuData.get(0).getDistance() + "KM");


                    final MaterialDialog materialDialog = new MaterialDialog(CalendarActivity.this);
                    materialDialog.setCanceledOnTouchOutside(true);
                    materialDialog.setContentView(view);
                    materialDialog.show();
                    mCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    Log.d("koma===bushu", bushuData.get(0).getBushu() + "");

                }

            }
        });
    }

    protected float pxTodp(float value) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float valueDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
        return valueDP;
    }

    private String getDate() {
//        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMd");
//        String date1 = dateFormat.format(date);
//        Log.d("koma---当前数据是",date1);
        String temp = mCurrentYear + "-" + mCurrentMonth + "-";
        return temp;
    }

    private void setData() {


        pointBar = null;


//        for (int i =1;i<mCurrentMonth)

        int days = 0;

        if (mCurrentMonth == 2) {
            days = 28;
            pointBar = new float[28];
        } else if (mCurrentMonth == 1 || mCurrentMonth == 3 || mCurrentMonth == 5 ||
                mCurrentMonth == 7 || mCurrentMonth == 8 || mCurrentMonth == 10 || mCurrentMonth
                == 12) {
            days = 31;
            pointBar = new float[31];
        } else {
            days = 30;
            pointBar = new float[30];
        }
        for (int i = 1; i <= days; i++) {
            List<BushuData> bushuDatas = DataSupport.where("riqi = ?", (getDate() + i)).find
                    (BushuData.class);

            if (bushuDatas.size() == 0) {
//                pointBar[i - 1] = 1;
            } else {
                if (bushuDatas.get(0).getBushu() == 0) {
//                    pointBar[i - 1] = 1;
                } else {
                    pointBar[i - 1] = bushuDatas.get(0).getBushu();
                }

            }

        }


        mBarChartView.setBarChartList(pointBar);
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.chenhang.month")) {
                int month = intent.getIntExtra("month", 1);
                mCurrentMonth = month;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                }, 500);
            }

            if (intent.getAction().equals("com.chenhang.year")) {
                int year = intent.getIntExtra("year", 2000);
                mCurrentYear = year;
            }


        }

    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
