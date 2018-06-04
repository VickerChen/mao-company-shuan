package com.moscase.shouhuan.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.MyInfoBean;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.view.CircleSeekBar;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;

public class TargetActivity extends AppCompatActivity {

    private TextView mQianka;

    private CircleSeekBar mSeekBar;

    private TextView mBushu;

    private SharedPreferences mSharedPreferences;

    private RelativeLayout mRelativeLayout;

    private TextView mWalk;
    private TextView mRun;
    private TextView mQinfen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        mWalk = (TextView) findViewById(R.id.walk);
        mRun = (TextView) findViewById(R.id.run);
        mSharedPreferences = getSharedPreferences("myInfo", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置目标");
        toolbar.setTitleTextColor(Color.GRAY);
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mQinfen = (TextView) findViewById(R.id.qinfen);

        mQianka = (TextView) findViewById(R.id.qianka);
        mSeekBar = (CircleSeekBar) findViewById(R.id.seekbar);
        mBushu = (TextView) findViewById(R.id.bushu);

        mSeekBar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                if (curValue > 12500)
                    mQinfen.setText("勤快");
                else
                    mQinfen.setText("懒惰");
                setQianKa(curValue);
                setWalk(curValue);
                setRun(curValue);
                mBushu.setText(curValue + "");
                mSharedPreferences.edit().putInt("mubiao", curValue).commit();
            }
        });

        mSeekBar.setCurProcess(mSharedPreferences.getInt("mubiao", 10000));


//        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
//        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TimePickerView pvTime = new TimePickerView.Builder(TargetActivity.this, new
// TimePickerView.OnTimeSelectListener() {
//                    @Override
//                    public void onTimeSelect(Date date, View v) {//选中事件回调
//
//                    }
//                }).build();
//                pvTime.setDate(Calendar.getInstance());
// 注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
//                pvTime.show();
//            }
//        });

    }

    private void setRun(int value) {
        //小时
        int int1 = value / (300 * 60);
        //分钟
        float double1 = value / 1.0f / (300 * 60);
        DecimalFormat df = new DecimalFormat("#.0000");
        double temp = Double.parseDouble(df.format(double1));
        String double_str = String.format("%.4f", temp);
        String intNumber = double_str.substring(double_str.indexOf(".") + 1, double_str.length());
        float float1 = Integer.parseInt(intNumber) / 1.0f / 10000;
        mRun.setText(int1 + "时" + (int) (float1 * 60) + "分");
    }

    private void setWalk(int value) {
        //小时
        int int1 = value / (50 * 60);
        //分钟
        float double1 = value / 1.0f / (50 * 60);
        DecimalFormat df = new DecimalFormat("#.0000");
        double temp = Double.parseDouble(df.format(double1));
        String double_str = String.format("%.4f", temp);
        String intNumber = double_str.substring(double_str.indexOf(".") + 1, double_str.length());
        float float1 = Integer.parseInt(intNumber) / 1.0f / 10000;

        mWalk.setText(int1 + "时" + (int) (float1 * 60) + "分");
    }

    private void setQianKa(int value) {
        double tizhong;
        if (MyApplication.isInch)
            tizhong = mSharedPreferences.getFloat("yingzhitizhong", 68);
        else
            tizhong = DataSupport.find(MyInfoBean.class, 1).getTizhong();
        long temp = Math.round(value * (tizhong * 0.000693 - 0.0045));
        mQianka.setText(temp + "");
    }
}
