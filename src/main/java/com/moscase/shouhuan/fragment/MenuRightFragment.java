package com.moscase.shouhuan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.AboutActivity;
import com.moscase.shouhuan.activity.ClockActivity;
import com.moscase.shouhuan.activity.MyInfoActivity;
import com.moscase.shouhuan.activity.TargetActivity;

/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
public class MenuRightFragment extends Fragment {
    private LinearLayout mMyInfo;
    private LinearLayout mTarget;
    private LinearLayout mClock;
    private LinearLayout mAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.menu_layout_right, container);
        mMyInfo = (LinearLayout) view.findViewById(R.id.myInfo);
        mMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent);
            }
        });

        mTarget = (LinearLayout) view.findViewById(R.id.target);
        mTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TargetActivity.class);
                startActivity(intent);
            }
        });

        mClock = (LinearLayout) view.findViewById(R.id.clock);
        mClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClockActivity.class);
                startActivity(intent);
            }
        });

        mAbout = (LinearLayout) view.findViewById(R.id.about);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}