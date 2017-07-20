package com.moscase.shouhuan.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.CircleView;
import com.moscase.shouhuan.view.RingView;
import com.moscase.shouhuan.view.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhuangtaiFragment extends Fragment {
    private ViewPager mViewPager;
    private View view1;
    private View view2;
    private List<View> viewList;
    private CircleIndicator mIndicator;
    private RingView mRingView;
    private CircleView mCircleView;
    public ZhuangtaiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhuangtai, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        view1 = inflater.inflate(R.layout.zhuangtai1, null);
        mRingView = (RingView) view1.findViewById(R.id.MiClockView);
        mCircleView = (CircleView) view1.findViewById(R.id.circleview);
        mRingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRingView.getIsAnimRunning()){
                    mRingView.startAnim();
                    mCircleView.startAnim();
                }
            }
        });
        view2 = inflater.inflate(R.layout.zhuangtai2, null);
        mIndicator = (CircleIndicator) view.findViewById(R.id.indicator);
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        mIndicator.setViewPager(mViewPager);
        // Inflate the layout for this fragment
        return view;
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

}
