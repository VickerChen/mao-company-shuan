package com.moscase.shouhuan.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.ToggleButton;

/**
 * Created by 陈航 on 2017/8/26.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class NotifyActivity extends Activity {

    private ToggleButton mToggleButton;
    private SharedPreferences mSharedPreferences;
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mSharedPreferences = getSharedPreferences("ToggleButton",MODE_PRIVATE);
        isChecked = mSharedPreferences.getBoolean("isChecked",true);
        if (isChecked){
            mToggleButton.setChecked(isChecked);
        }
        mToggleButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleButton view, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(NotifyActivity.this, "成功", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("isChecked",isChecked).commit();

            }
        });
    }
}
