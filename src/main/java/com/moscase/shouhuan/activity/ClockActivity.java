package com.moscase.shouhuan.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.moscase.shouhuan.R;

/**
 * 这个Activity是小胡让我直接从WiFiWeather上搬过来的，
 * 我没有分析的太详细，直接把有用的抄过来
 */
public class ClockActivity extends AppCompatActivity {
    private TimePicker mTimePicker;
    private String mTimeFormat;
    private ToggleButton Alarmbtn1, Alarmbtn2, Snoozebtn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean alarm1, alarm2, isSnooze;
    ToggleStatus status = new ToggleStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置闹钟");
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

        preferences = getSharedPreferences("togglebuttonstatus",
                Context.MODE_PRIVATE);
        alarm1 = preferences.getBoolean("alarm1", false);
        alarm2 = preferences.getBoolean("alarm2", false);

        isSnooze = preferences.getBoolean("isSnooze", false);
        editor = preferences.edit();


        mTimePicker = (TimePicker) findViewById(R.id.timePic1);
        Alarmbtn1 = (ToggleButton) findViewById(R.id.AlarmBtn1);
        Alarmbtn2 = (ToggleButton) findViewById(R.id.AlarmBtn2);
        Snoozebtn = (ToggleButton) findViewById(R.id.SnoozeBtn);
        Alarmbtn1.setChecked(alarm1);
        Alarmbtn2.setChecked(alarm2);
        Snoozebtn.setChecked(isSnooze);

        if (!alarm1 && !alarm2) {
            Snoozebtn.setClickable(false);
        }
        mTimePicker.setCurrentHour(Integer.valueOf(preferences.getString("hour", "12")));
        mTimePicker.setCurrentMinute(Integer.valueOf(preferences.getString("min", "00")));
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                Log.d("日期是---hour", hour + "");
                Log.d("日期是---min", minute + "");
                mTimePicker.setCurrentHour(hour);
                mTimePicker.setCurrentMinute(minute);
                if (hour < 10)
                    editor.putString("hour", "0" + hour);
                else
                    editor.putString("hour", hour + "");

                if (minute < 10)
                    editor.putString("min", "0" + minute);
                else
                    editor.putString("min", minute+"");

//                editor.putInt("min", minute);
                editor.commit();
            }
        });
        mTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        //获得系统的时间制式
        ContentResolver cv = this.getContentResolver();
        mTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (("24").equals(mTimeFormat)) {
            //设置24小时制式
            mTimePicker.setIs24HourView(true);
        } else {
            mTimePicker.setIs24HourView(false);
        }
        setListener();
    }

    private void setListener() {
        //闹钟开关的点击按钮
        Alarmbtn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean check) {
                status.setOne(check);
                //闹钟1打开  闹钟2关闭     贪睡按钮能点击
                if (check) {
                    Alarmbtn2.setChecked(false);
                    Snoozebtn.setClickable(true);
                } else {
                    //闹钟2打 闹钟1关闭      贪睡按钮能点击开
                    if (Alarmbtn2.isChecked()) {
                        Snoozebtn.setClickable(true);
                    } else {
                        //闹钟都关闭的时候    强制贪睡关闭  并且贪睡不能点击
                        Snoozebtn.setChecked(false);
                        Snoozebtn.setClickable(false);
                    }

                }
                preferences.edit().putBoolean("alarm1", check).commit();
            }
        });

        Alarmbtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean check) {
                status.setOne(check);
                if (check) {
                    Alarmbtn1.setChecked(false);
                    Snoozebtn.setClickable(true);
                } else {
                    if (Alarmbtn1.isChecked()) {
                        Snoozebtn.setClickable(true);
                    } else {
                        Snoozebtn.setChecked(false);
                        Snoozebtn.setClickable(false);
                    }

                }
                preferences.edit().putBoolean("alarm2", check).commit();
            }
        });

        //贪睡的点击事件
        Snoozebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean check) {
                preferences.edit().putBoolean("isSnooze", check).commit();
            }
        });
    }

    public class ToggleStatus {
        public boolean one;
        public boolean two;
        public boolean three;
        public boolean four;

        public boolean isOne() {
            return one;
        }

        public void setOne(boolean one) {
            this.one = one;
        }

        public boolean isTwo() {
            return two;
        }

        public void setTwo(boolean two) {
            this.two = two;
        }
    }
}
