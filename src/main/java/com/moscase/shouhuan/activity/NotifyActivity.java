package com.moscase.shouhuan.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.ToggleButton;

/**
 * Created by 陈航 on 2017/8/26.
 *
 * 这个界面是长短连接界面
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class NotifyActivity extends AppCompatActivity {

    private ToggleButton mToggleButton;
    private ToggleButton mToggleButtonJibu;
    private SharedPreferences mSharedPreferences;
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mToggleButtonJibu = (ToggleButton) findViewById(R.id.toggleButton_jibu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("连接设置");
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
        mSharedPreferences = getSharedPreferences("ToggleButton",MODE_PRIVATE);
        isChecked = mSharedPreferences.getBoolean("isChecked",true);
        if (isChecked){
            mToggleButton.setChecked(true);
        }else {
            mToggleButtonJibu.setChecked(true);
        }
        mToggleButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleButton view, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("isChecked",true).commit();
                    mToggleButtonJibu.setChecked(false);
                }else {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("isChecked",false).commit();
                    mToggleButtonJibu.setChecked(true);
                }
            }
        });

        mToggleButtonJibu.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleButton view, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("isChecked",false).commit();
                    mToggleButton.setChecked(false);
                }else {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("isChecked",true).commit();
                    mToggleButton.setChecked(true);
                }

            }
        });
    }
}
