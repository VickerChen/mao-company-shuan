package com.moscase.shouhuan.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gelitenight.waveview.library.WaveView;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.WaveHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ZhibiaoFragment extends Fragment {
    private List<Float> mCollectionsValues = new ArrayList<>();
    private RadarView mRadarView;
    private RadarData mData;
    private List<String> mTextList = new ArrayList<>();
    private int tizhilv;
    private int yaotunbi;
    private int quzhitizhong;
    private int jichudaixie;
    private int bmi;
    private int shuifen;

    private float tizhilvfloat;
    private float yaotunbifloat;
    private float quzhitizhongfloat;
    private float jichudaixiefloat;
    private float bmifloat;
    private float shuifenfloat;

    private WaveView mWaveView;
    private WaveHelper mWaveHelper;


    private LinearLayout mLinearLayout;
    public ZhibiaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zhibiao, container, false);
        //给雷达图使用的float参数
        initFloat();

        //给雷达图6个角使用的int参数
        initInt();

        //把雷达图的描述值存进list
        initList();

        initView(view);

        return view;
    }

    private void initInt() {
        tizhilv = (int) tizhilvfloat;
        yaotunbi = (int) yaotunbifloat;
        quzhitizhong = (int) quzhitizhongfloat;
        jichudaixie = (int) jichudaixiefloat;
        bmi = (int) bmifloat;
        shuifen = (int) shuifenfloat;
    }

    private void initFloat() {
        tizhilvfloat = (float) Math.random() * 100;
        yaotunbifloat = (float) Math.random() * 100;
        quzhitizhongfloat = (float) Math.random() * 100;
        jichudaixiefloat = (float) Math.random() * 100;
        bmifloat = (float) Math.random() * 100;
        shuifenfloat = (float) Math.random() * 100;
    }

    private void initList() {
        mTextList.add("体脂率 " + tizhilv);
        mTextList.add("腰臀比 " + yaotunbi);
        mTextList.add("去脂体重 " + quzhitizhong);
        mTextList.add("基础代谢 " + jichudaixie);
        mTextList.add("BMI " + bmi);
        mTextList.add("水份 " + shuifen);
    }

    private void initView(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.two);

        mRadarView = (RadarView) view.findViewById(R.id.radarView);
//          设置雷达图的圆形或者正多边形
//        mRadarView.setWebMode(RadarView.WEB_MODE_CIRCLE);
        mRadarView.setMaxValue(100);
        Collections.addAll(mCollectionsValues, tizhilvfloat, yaotunbifloat, quzhitizhongfloat,
                jichudaixiefloat, bmifloat, shuifenfloat);
        mData = new RadarData(mCollectionsValues);
        mData.setColor(0xffffff);
        mRadarView.addData(mData);
        mRadarView.setVertexText(mTextList);
        mRadarView.setRadarLineWidth(1);
        mRadarView.setLayer(4);

        mWaveView = (WaveView) view.findViewById(R.id.wave);
        mWaveHelper = new WaveHelper(mWaveView);
        mWaveView.setBorder(1, Color.GRAY);
        mWaveView.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveHelper.setVertic(shuifenfloat/100);
        mWaveHelper.initAnimation();
        mWaveHelper.start();

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initFloat();

                initInt();

                mTextList.clear();

                initList();

                mCollectionsValues.clear();

                Collections.addAll(mCollectionsValues, tizhilvfloat, yaotunbifloat, quzhitizhongfloat,
                        jichudaixiefloat, bmifloat, shuifenfloat);

                mData = new RadarData(mCollectionsValues);

                mRadarView.animeValue(2000,mData);

                mWaveHelper.cancel();
                mWaveHelper.setVertic(shuifenfloat/100);
                mWaveHelper.initAnimation();
                mWaveHelper.start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mRadarView.animeValue(2000, mData);
    }

    @Override
    public void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden){
            mWaveHelper.cancel();
        }else {
            mWaveHelper.start();
        }
    }
}
