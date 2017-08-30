package com.moscase.shouhuan.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.CalendarActivity;
import com.moscase.shouhuan.activity.NotifyActivity;
import com.moscase.shouhuan.activity.PhotoActivity;

/**
 * Created by 陈航 on 2017/8/3.
 *
 * 少年一事能狂  敢骂天地不仁
 */
public class MenuLeftFragment extends Fragment {
    private LinearLayout mCalendarLinearLayout;
    private LinearLayout mPhotoLinearLayout;
    private LinearLayout mNotifyLayout;

    private String[] permissionsLocation = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu, container, false);

        mCalendarLinearLayout = (LinearLayout) view.findViewById(R.id.calendar);
        mCalendarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        mPhotoLinearLayout = (LinearLayout) view.findViewById(R.id.takePhoto);
        mPhotoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                startActivity(intent);

            }
        });

        mNotifyLayout = (LinearLayout) view.findViewById(R.id.notify);
        mNotifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotifyActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
