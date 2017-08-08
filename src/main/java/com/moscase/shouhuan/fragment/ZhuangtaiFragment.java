package com.moscase.shouhuan.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.DistanceActivity;
import com.moscase.shouhuan.utils.PermissionUtil;
import com.moscase.shouhuan.view.CircleView;
import com.moscase.shouhuan.view.RadarData;
import com.moscase.shouhuan.view.RadarView;
import com.moscase.shouhuan.view.RingView;
import com.moscase.shouhuan.view.ZoomOutPageTransformer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhuangtaiFragment extends Fragment {
    private ViewPager mViewPager;
    private View mZhuangtai1View;
    private View mZhuangtai2View;
    private List<View> viewList;
    private CircleIndicator mIndicator;
    private RingView mRingView;
    private CircleView mCircleView;
    private TextView mDate;
    private RadarView mRadarView;
    private LinearLayout mLinearLayout;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public ZhuangtaiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhuangtai, container, false);
        mIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mZhuangtai1View = inflater.inflate(R.layout.zhuangtai1, null);
        mZhuangtai2View = inflater.inflate(R.layout.zhuangtai2, null);

        initZhuangtai1View(mZhuangtai1View);
        initZhuangtai2View(mZhuangtai2View);

        viewList = new ArrayList<>();
        viewList.add(mZhuangtai1View);
        viewList.add(mZhuangtai2View);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mIndicator.setViewPager(mViewPager);
        return view;
    }

    private void initZhuangtai2View(View view) {
        mRadarView = (RadarView) view.findViewById(R.id.radarview);
        List<RadarData> dataList = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            RadarData data = new RadarData("标题" + i, i * 11);
            dataList.add(data);
        }
        mRadarView.setDataList(dataList);

    }

    private void initZhuangtai1View(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.guiji);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //6.0之后动态申请权限
                    showContacts(mLinearLayout);
                } else {
                    init();
                }
            }
        });
        mDate = (TextView) view.findViewById(R.id.date);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        mDate.setText(date);
        mRingView = (RingView) view.findViewById(R.id.MiClockView);
        mCircleView = (CircleView) view.findViewById(R.id.circleview);
        mRingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRingView.getIsAnimRunning()) {
                    mRingView.startAnim();
                    mCircleView.startAnim();
                }
            }
        });
    }

    //ViewPager的Adapter
    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };

    public void showContacts(View v) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(v);
        } else {
            init();
        }
    }

    private void requestPermissions(View v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_PHONE_STATE)
                ) {
            Snackbar.make(v, "需要申请权限才能完成定位",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(getActivity(), permissions,
                                            123);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, 123);
        }
    }

    private void init() {
        Intent intent = new Intent(getActivity(), DistanceActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                init();
            } else {
                Toast.makeText(getActivity(), "请授予权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
