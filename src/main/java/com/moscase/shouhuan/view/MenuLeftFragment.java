package com.moscase.shouhuan.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.CalendarActivity;



public class MenuLeftFragment extends Fragment {
    private LinearLayout mCalendarLinearLayout;

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
        return view;
    }
}
