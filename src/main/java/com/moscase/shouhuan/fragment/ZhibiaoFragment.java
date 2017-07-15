package com.moscase.shouhuan.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moscase.shouhuan.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ZhibiaoFragment extends Fragment {


    public ZhibiaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zhibiao, container, false);

        return view;
    }

}
