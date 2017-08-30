package com.moscase.shouhuan.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.BMIView;
import com.moscase.shouhuan.view.PieChart;
import com.moscase.shouhuan.view.RulerView;
import com.moscase.shouhuan.view.WaveHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;


/**
 * Created by 陈航 on 2017/7/20.
 *
 * 少年一事能狂  敢骂天地不仁
 */
@SuppressLint("ValidFragment")
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
    private Context mContext;
    private float tizhilvfloat;
    private float yaotunbifloat;
    private float quzhitizhongfloat;
    private float jichudaixiefloat;
    private float bmifloat;
    private float shuifenfloat;

    private WaveView mWaveView;
    private WaveHelper mWaveHelper;

    private BMIView mBMIView;

    private RulerView mRulerView;

    private TextView mTiZhong;

    private PieChart mPieChart;

    private TextView mShuifen;

    private LinearLayout mLinearLayout;
    @SuppressLint("ValidFragment")
    public ZhibiaoFragment(Context context) {
        // Required empty public constructor
        mContext = context;
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
        quzhitizhongfloat = Math.max(new Random().nextFloat()*100,40);
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
        mPieChart = (PieChart) view.findViewById(R.id.piechart);
        mPieChart.initSrc(new float[]{20f,30f,40f}, new String[]{"#ff80FF",
                "#ffFF00", "#6A5ACD"});

        mTiZhong = (TextView) view.findViewById(R.id.tizhong);
        mRulerView = (RulerView) view.findViewById(R.id.ruler_height);
        mRulerView.setValue(quzhitizhongfloat, 40, 200, 1);
        mRulerView.setClickable(false);

        mShuifen = (TextView) view.findViewById(R.id.shuifentext);
        mShuifen.setText("水份: " + shuifen + "%");

        mTiZhong.setText("去脂体重:"+quzhitizhongfloat+"KG");

        mLinearLayout = (LinearLayout) view.findViewById(R.id.takePhoto);

        mBMIView = (BMIView) view.findViewById(R.id.bmi);
        mBMIView.setCreditValueWithAnim(bmi);

        mRadarView = (RadarView) view.findViewById(R.id.radarView);
//          设置雷达图的圆形或者正多边形
//        mRadarView.setWebMode(RadarView.WEB_MODE_CIRCLE);
        mRadarView.setMaxValue(100);
        Collections.addAll(mCollectionsValues, tizhilvfloat, yaotunbifloat, quzhitizhongfloat,
                jichudaixiefloat, bmifloat, shuifenfloat);
        mData = new RadarData(mCollectionsValues);
        mData.setColor(Color.argb(255,255,255,255));
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

                mBMIView.setCreditValueWithAnim(bmi);
                mRulerView.setValue(quzhitizhongfloat, 40, 200, 1);
                mTiZhong.setText("去脂体重:"+quzhitizhongfloat+"KG");
                mShuifen.setText("水份: " + shuifen + "%");
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
            mBMIView.setCreditValueWithAnim(bmi);
        }
    }
}
