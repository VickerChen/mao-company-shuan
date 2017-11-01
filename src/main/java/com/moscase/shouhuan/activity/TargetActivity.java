package com.moscase.shouhuan.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.CircleSeekBar;

public class TargetActivity extends AppCompatActivity {

    private TextView mQianka;

    private CircleSeekBar mSeekBar;

    private TextView mBushu;

    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        mSharedPreferences = getSharedPreferences("myinfo",MODE_PRIVATE);
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

        mQianka = (TextView) findViewById(R.id.qianka);
        mSeekBar = (CircleSeekBar) findViewById(R.id.seekbar);
        mBushu = (TextView) findViewById(R.id.bushu);

        mSeekBar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                mQianka.setText(curValue+"");
                mBushu.setText(curValue+"");
                mSharedPreferences.edit().putInt("mubiao",curValue).commit();
            }
        });

        mSeekBar.setCurProcess(mSharedPreferences.getInt("mubiao",10000));

    }
}
