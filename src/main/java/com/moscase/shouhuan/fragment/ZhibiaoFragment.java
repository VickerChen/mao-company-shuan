package com.moscase.shouhuan.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.utils.ComputeMyInfo;
import com.moscase.shouhuan.view.BMIView;
import com.moscase.shouhuan.view.PieChart;
import com.moscase.shouhuan.view.RulerView;
import com.moscase.shouhuan.view.WaveHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;


/**
 * Created by 陈航 on 2017/7/20.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
@SuppressLint("ValidFragment")
public class ZhibiaoFragment extends Fragment {
    private List<Float> mCollectionsValues = new ArrayList<>();
    private RadarView mRadarView;
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
    private boolean isFirst = false;

    @SuppressLint("ValidFragment")
    public ZhibiaoFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zhibiao, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initFloat();

        initInt();

        initList();

        initData();
    }

    private void initFloat() {
        //体脂率在计算出来的时候要是小数，因为后续计算需要用到，所以在绘制界面的时候就要乘100
        tizhilvfloat = ComputeMyInfo.getInstance().computeTizhilv() * 100;
        yaotunbifloat = ComputeMyInfo.getInstance().computeYaotunbi();
        quzhitizhongfloat = ComputeMyInfo.getInstance().computeQuzhitizhong();
        jichudaixiefloat = ComputeMyInfo.getInstance().computeJichudaixielv() > 100 ?
                100 : ComputeMyInfo.getInstance().computeJichudaixielv();
        bmifloat = ComputeMyInfo.getInstance().computeBMI();
        shuifenfloat = ComputeMyInfo.getInstance().computeShuifen();
    }

    private void initInt() {
        tizhilv = (int) tizhilvfloat;
        yaotunbi = (int) yaotunbifloat;
        quzhitizhong = (int) quzhitizhongfloat;
        jichudaixie = (int) jichudaixiefloat > 100 ? 100 : (int) jichudaixiefloat;
        bmi = (int) bmifloat;
        shuifen = (int) shuifenfloat;
    }

    private void initList() {
        mTextList.clear();
        mTextList.add("体脂率 " + tizhilv + "%");
        mTextList.add("腰臀比 " + yaotunbi + "%");
        mTextList.add("去脂体重 " + quzhitizhong);
        mTextList.add("基础代谢 " + (int)ComputeMyInfo.getInstance().computeJichudaixielv());
        mTextList.add("BMI " + bmi);
        mTextList.add("水份 " + shuifen + "%");
    }


    private void initData() {
        mPieChart.initSrc(new float[]{20f, 30f, 40f}, new String[]{"#ff80FF",
                "#ffFF00", "#6A5ACD"});

        mRulerView.setValue(quzhitizhongfloat, 40, 200, 1);
        mRulerView.setClickable(false);

        mShuifen.setText("水份: " + shuifen + "%");

        mTiZhong.setText("去脂体重:" + quzhitizhongfloat + "KG");
        mBMIView.setCreditValueWithAnim(bmi);
        mRadarView.setMaxValue(100);

        //          设置雷达图的圆形或者正多边形
//        mRadarView.setWebMode(RadarView.WEB_MODE_CIRCLE);
        mCollectionsValues.clear();
        Collections.addAll(mCollectionsValues, tizhilvfloat, yaotunbifloat, quzhitizhongfloat,
                jichudaixiefloat, bmifloat, shuifenfloat);
        Log.d("size", mCollectionsValues.size() + "");
        RadarData mData = new RadarData(mCollectionsValues);
        mData.setColor(Color.argb(255, 255, 255, 255));
        //这里不做first判断的话直接addData的时候会造成多次布局，即雷达图的颜色会越来越白，就是多重数据叠加
        if (!isFirst) {
            mRadarView.addData(mData);
            isFirst = true;
        } else {
            mRadarView.animeValue(2000, mData);
        }
        mRadarView.setVertexText(mTextList);
        mRadarView.setRadarLineWidth(1);
        mRadarView.setLayer(4);

        mWaveHelper = new WaveHelper(mWaveView);
        mWaveView.setBorder(1, Color.GRAY);
        mWaveView.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveHelper.setVertic(shuifenfloat / 100);
        mWaveHelper.initAnimation();
        mWaveHelper.start();
    }

    private void initView(View view) {
        mPieChart = (PieChart) view.findViewById(R.id.piechart);
        mTiZhong = (TextView) view.findViewById(R.id.tizhong);
        mRulerView = (RulerView) view.findViewById(R.id.ruler_height);
        mShuifen = (TextView) view.findViewById(R.id.shuifentext);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.takePhoto);
        mBMIView = (BMIView) view.findViewById(R.id.bmi);
        mRadarView = (RadarView) view.findViewById(R.id.radarView);
        mWaveView = (WaveView) view.findViewById(R.id.wave);

    }


    @Override
    public void onPause() {
        super.onPause();
        mWaveHelper.cancel();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            mWaveHelper.cancel();
        } else {
            mWaveHelper.start();
            mBMIView.setCreditValueWithAnim(bmi);
        }
    }
}
