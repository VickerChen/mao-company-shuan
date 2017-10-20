package com.moscase.shouhuan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moscase.shouhuan.R;

public class AboutActivity extends AppCompatActivity {

    private TextView mVersion;

    private RelativeLayout mRl_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mVersion = (TextView) findViewById(R.id.version);
        mVersion.setText(R.string.version);

        mRl_email = (RelativeLayout) findViewById(R.id.rl_email);
        mRl_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:sales@moscase8.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "User feedback");
                data.putExtra(Intent.EXTRA_TEXT,
                        "Please enter your comments or Suggestions");
                startActivity(data);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
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
    }
}
