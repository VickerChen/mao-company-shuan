package com.moscase.shouhuan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.moscase.shouhuan.R;

public class SplashActivity extends Activity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();

        final Intent intent = new Intent(this,MainActivity.class);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        },2000);

    }
}
