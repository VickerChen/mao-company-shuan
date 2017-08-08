package com.moscase.shouhuan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.moscase.shouhuan.R;

import java.text.SimpleDateFormat;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;


public class CalendarActivity extends Activity {

    private DatePicker mDatePicker;

    private String[] mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mDatePicker = (DatePicker) findViewById(R.id.datepicker);

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

}
