package com.moscase.shouhuan.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.MyInfoBean;
import com.moscase.shouhuan.utils.ComputeMyInfo;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.view.BMIView;
import com.moscase.shouhuan.view.PieChart;
import com.moscase.shouhuan.view.RadarData;
import com.moscase.shouhuan.view.RadarView;
import com.moscase.shouhuan.view.RulerView;
import com.moscase.shouhuan.view.WaveHelper;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 陈航 on 2017/7/20.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
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
    private float bmi;
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
    private PieChart mPieChart;
    private ImageView mIv_tixing;
    private TextView mShuifen;
    private TextView mXingzhuang;
    private LinearLayout mLinearLayout;
    private boolean isFirst = false;
    private List<RadarData> mDataList;
    private SharedPreferences mSharedPreferences;
    private float mShuifenBi;
    private float mJichudaixieBi;


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

//        initList();

        initData();
    }

    private void initFloat() {
        if (MyApplication.isInch) {
            //体脂率在计算出来的时候要是小数，因为后续计算需要用到，所以在绘制界面的时候就要乘100
            tizhilvfloat = computeTizhilv() * 100;
            yaotunbifloat = (float) computeYaotunbi();
            quzhitizhongfloat = (float) computeQuzhitizhong();
            jichudaixiefloat = computeJichudaixielv();
            bmifloat = computeBMI();
            shuifenfloat = computeShuifen() > 100 ? 100 : computeShuifen();
        } else {
            tizhilvfloat = ComputeMyInfo.getInstance().computeTizhilv() * 100;
            yaotunbifloat = (float) ComputeMyInfo.getInstance().computeYaotunbi();
            quzhitizhongfloat = (float) ComputeMyInfo.getInstance().computeQuzhitizhong();
            jichudaixiefloat = ComputeMyInfo.getInstance().computeJichudaixielv();
            bmifloat = ComputeMyInfo.getInstance().computeBMI();
            shuifenfloat = ComputeMyInfo.getInstance().computeShuifen() > 100 ? 100 :
                    ComputeMyInfo.getInstance().computeShuifen();
        }

    }

    private void initInt() {
        tizhilv = Math.round(tizhilvfloat);
        if (tizhilv < 0)
            tizhilv = 0;
        yaotunbi = Math.round(yaotunbifloat);
        quzhitizhong = Math.round(quzhitizhongfloat);
        if (quzhitizhong > 100)
            quzhitizhong = 100;
        else if (quzhitizhong < 0)
            quzhitizhong = 0;
        jichudaixie = Math.round(jichudaixiefloat > 100 ? 100 : (int) jichudaixiefloat);

        shuifen = Math.round(shuifenfloat);
    }

//    private void initList() {
//        mTextList.clear();
//        mTextList.add("体脂率 " + tizhilv + "%");
//        mTextList.add("腰臀比 " + yaotunbi / 1.0f / 100);
//        mTextList.add("去脂体重 " + quzhitizhong);
//        mTextList.add("基础代谢率 " + (int) ComputeMyInfo.getInstance().computeJichudaixielv());
//        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
//            bmi = (int) Math.round(bmifloat * 22.5 / 100);
//        } else {
//            bmi = (int) Math.round(bmifloat * 21.5 / 100);
//        }
//        mTextList.add("BMI " + bmi);
//        mTextList.add("水份 " + shuifen + "%");
//    }


    private void initData() {
        float yaowei = (float) DataSupport.find(MyInfoBean.class, 1).getYaowei();
//        float tunwei = (float) DataSupport.find(MyInfoBean.class, 1).getTunwei();
        mPieChart.initSrc(new float[]{yaowei, yaotunbifloat > 100 ? 0 : 100 - yaotunbifloat}, new
                String[]{"#ff80FF",
                "#ffFF00"});

        if (yaotunbifloat >= 75 && yaotunbifloat < 85) {
            mXingzhuang.setText("正常体型");
            mIv_tixing.setImageResource(R.drawable.tixing2);
        } else if (yaotunbifloat < 75) {
            mXingzhuang.setText("梨型");
            mIv_tixing.setImageResource(R.drawable.tixing1);
        } else {
            mXingzhuang.setText("苹果型");
            mIv_tixing.setImageResource(R.drawable.tixing3);
        }


        mRulerView.setValue(quzhitizhong, 0, 200, 1);
        mRulerView.setClickable(false);

        mShuifen.setText("水份: " + shuifen + "%");


        float temp = 0;
        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            temp = bmifloat / 22.5f;
        } else {
            temp = bmifloat / 21.5f;
        }

        if (temp < 1 && temp > 0)
            bmi = temp;
        else if (temp > 1 && temp <= 2)
            bmi = 2 - temp;
        else if (temp > 2)
            bmi = 1;
        else if (temp < 0)
            bmi = 0;
        bmi = bmi * 100;

//        if (bmi < 0)
//            bmi = 0;
        Log.d("koma---BMIIS", "" + bmi);
        mBMIView.setCreditValueWithAnim(Math.round(bmi >
                100 ? 100 : bmi));


        mDataList = new ArrayList<>();

        initRadar();
        mRadarView.setDataList(mDataList);

        mWaveHelper = new WaveHelper(mWaveView);
        mWaveView.setBorder(1, Color.GRAY);
        mWaveView.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveHelper.setVertic(shuifenfloat / 100 >= 0.95 ? (float) (shuifenfloat / 100 - 0.02) :
                shuifenfloat / 100);
        mWaveHelper.initAnimation();
        mWaveHelper.start();
    }

    private void initView(View view) {
        mIv_tixing = view.findViewById(R.id.iv_tixing);
        mPieChart = view.findViewById(R.id.piechart);
        mRulerView = view.findViewById(R.id.ruler_height);
        mShuifen = view.findViewById(R.id.shuifentext);
        mLinearLayout = view.findViewById(R.id.takePhoto);
        mBMIView = view.findViewById(R.id.bmi);
        mRadarView = view.findViewById(R.id.radarView);
        mWaveView = view.findViewById(R.id.wave);
        mXingzhuang = view.findViewById(R.id.xingzhuang);
        mSharedPreferences = getActivity().getSharedPreferences("myInfo", Context.MODE_PRIVATE);
    }

    private void initRadar() {
        mDataList.clear();
        float tempQuzhitizhong = quzhitizhongfloat / 39.75f;
        if (tempQuzhitizhong > 1 && tempQuzhitizhong < 2)
            tempQuzhitizhong = 2 - tempQuzhitizhong;
        else if (tempQuzhitizhong < 0)
            tempQuzhitizhong = 0;
        else if (tempQuzhitizhong > 2)
            tempQuzhitizhong = 1;

        initShuifenBi();
        initJichudaixieBi();

        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            if (DataSupport.find(MyInfoBean.class, 1).getBirthday() <= 30) {
                tizhilvfloat = tizhilvfloat / 17.0f;
            } else {
                tizhilvfloat = tizhilvfloat / 20.0f;
            }
        } else {
            if (DataSupport.find(MyInfoBean.class, 1).getBirthday() <= 30) {
                tizhilvfloat = tizhilvfloat / 20.5f;
            } else {
                tizhilvfloat = tizhilvfloat / 23.5f;
            }
        }

        float temp = 0;
        Log.d("koma---shuifenbi", "" + shuifenfloat / mShuifenBi);
        if (shuifenfloat / mShuifenBi < 1 && shuifenfloat / mShuifenBi > 0)
            temp = shuifenfloat / mShuifenBi;
        else if (shuifenfloat / mShuifenBi >= 1 && shuifenfloat / mShuifenBi < 2)
            temp = 2 - shuifenfloat / mShuifenBi;
        else if (shuifenfloat / mShuifenBi > 2)
            temp = 1;
        else if (shuifenfloat / mShuifenBi < 0)
            temp = 0;

        float tempjichudaixie = 0;
        if (jichudaixiefloat / 1.0f / mJichudaixieBi < 1 && jichudaixiefloat / 1.0f /
                mJichudaixieBi > 0)
            tempjichudaixie = jichudaixiefloat / 1.0f / mJichudaixieBi;
        else if (jichudaixiefloat / 1.0f / mJichudaixieBi >= 1 && jichudaixiefloat / 1.0f /
                mJichudaixieBi < 2)
            tempjichudaixie = 2 - jichudaixiefloat / 1.0f / mJichudaixieBi;
        else if (jichudaixiefloat / 1.0f / mJichudaixieBi > 2)
            tempjichudaixie = 1;
        else if (jichudaixiefloat / 1.0f / mJichudaixieBi < 0)
            tempjichudaixie = 0;


        float tempyaotunbi = 0;
        if (yaotunbifloat / 100 / 0.8 > 0 && yaotunbifloat / 100 / 0.8 < 1)
            tempyaotunbi = (float) (yaotunbifloat / 100 / 0.8);
        else if (yaotunbifloat / 100 / 0.8 >= 1 && yaotunbifloat / 100 / 0.8 < 2)
            tempyaotunbi = (float) (2 - yaotunbifloat / 100 / 0.8);
        else if (yaotunbifloat / 100 / 0.8 > 2)
            tempyaotunbi = 1;
        else if (yaotunbifloat / 100 / 0.8 < 0)
            tempyaotunbi = 0;
        Log.d("koma---jichudaixieis", "" + jichudaixiefloat);
        Log.d("koma---jichudaixieis", "" + mJichudaixieBi);
        Log.d("koma---jichudaixieis", "" + tempjichudaixie);
        Log.d("koma---tempyaotunbi", "" + tempyaotunbi);


        DecimalFormat df = new DecimalFormat("##0.0");
        if (MyApplication.isInch) {
            RadarData data = new RadarData("体脂率 " + (tizhilv > 100 ? 100 : tizhilv) + "%",
                    tizhilvfloat * 100 > 100 ? 100 : tizhilvfloat);
            mDataList.add(data);

            RadarData data1 = new RadarData("腰臀比 " + df.format(yaotunbi / 1.0f / 100),
                    tempyaotunbi * 100 > 100 ? 100 : tempyaotunbi * 100);
            mDataList.add(data1);

            RadarData data2 = new RadarData("去脂体重 " + quzhitizhong, tempQuzhitizhong * 100);
            mDataList.add(data2);

            RadarData data3 = new RadarData("基础代谢率 " + Math.round(jichudaixiefloat),
                    tempjichudaixie * 100);
            mDataList.add(data3);

            RadarData data4 = new RadarData("BMI  " + Math.round(bmifloat), bmi < 0 ? 0 : bmi);
            mDataList.add(data4);


            RadarData data5 = new RadarData("水份 " + shuifen + "%",
                    temp * 100);
            mDataList.add(data5);
        } else {
            RadarData data = new RadarData("体脂率 " + (tizhilv > 100 ? 100 : tizhilv) + "%",
                    tizhilvfloat * 100 > 100 ? 100 : tizhilvfloat);
            mDataList.add(data);

            RadarData data1 = new RadarData("腰臀比 " + df.format(yaotunbi / 1.0f / 100),
                    tempyaotunbi * 100 > 100 ? 100 : tempyaotunbi * 100);
            mDataList.add(data1);

            RadarData data2 = new RadarData("去脂体重 " + quzhitizhong, tempQuzhitizhong * 100);
            mDataList.add(data2);

            RadarData data3 = new RadarData("基础代谢率 " + Math.round(jichudaixiefloat),
                    tempjichudaixie * 100);
            mDataList.add(data3);

            RadarData data4 = new RadarData("BMI  " + Math.round(bmifloat), bmi < 0 ? 0 : bmi);
            mDataList.add(data4);

            RadarData data5 = new RadarData("水份 " + shuifen + "%",
                    temp * 100);
            mDataList.add(data5);
        }


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
            mBMIView.setCreditValueWithAnim(Math.round(bmi)
                    > 100 ? 100 : Math.round(bmi));
        }
    }


    public float computeBMI() {
        float BMI = (float) Math.rint(mSharedPreferences.getFloat("yingzhitizhong", 68) / (
                (mSharedPreferences.getFloat("yingzhishengao", 180) /
                        1.0f / 100) * (mSharedPreferences.getFloat("yingzhishengao", 180) / 1.0f /
                        100)));
        Log.d("koma", "BMI---" + BMI);
        return BMI;
    }

    public float computeTizhilv() {
        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            //参数a = 腰围公分（腰部的周长）*0.74
            double a = mSharedPreferences.getFloat("yingzhiyaowei", 70) * 0.74;
            //参数b = （总体重公斤*0.082）+ 44.74
            double b = mSharedPreferences.getFloat("yingzhitizhong", 68) * 0.082 + 44.74;
            //身体脂肪总重量公斤= a - b
            double c = a - b;
            //身体脂肪百分比 = （身体脂肪总重量/体重）* 100%
            double d = c / mSharedPreferences.getFloat("yingzhitizhong", 68);
            Log.d("koma", "体脂率---a" + a);
            Log.d("koma", "体脂率---b" + b);
            Log.d("koma", "体脂率---c" + b);
            Log.d("koma", "体脂率---男" + d);
            if (d < 0)
                d = 0;
            return (float) d;
        } else {
            //参数a = 腰围公分（腰部的周长）*0.74
            double a = mSharedPreferences.getFloat("yingzhiyaowei", 70) * 0.74;
            //参数b = （总体重公斤*0.082）+ 34.89
            double b = mSharedPreferences.getFloat("yingzhitizhong", 68) * 0.082 + 34.89;
            //身体脂肪总重量公斤= a - b
            double c = a - b;
            //身体脂肪百分比 = （身体脂肪总重量/体重）* 100%
            double d = c / mSharedPreferences.getFloat("yingzhitizhong", 68);
            Log.d("koma", "体脂率---女腰围" + mSharedPreferences.getFloat("yingzhiyaowei", 70));
            Log.d("koma", "体脂率---女" + d);
            if (d < 0)
                d = 0;
            return (float) d;
        }
    }

    public float computeJichudaixielv() {
        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            double a = 66 + (13.7 * mSharedPreferences.getFloat("yingzhitizhong", 68)) + (5 *
                    mSharedPreferences.getFloat("yingzhishengao", 180)) - (6.8 * ComputeMyInfo
                    .getInstance().getBirthday());
            Log.d("koma", "基础代谢---" + a);
            return (float) a;
        } else {
            double a = 655 + (9.6 * mSharedPreferences.getFloat("yingzhitizhong", 68)) + (1.7 *
                    mSharedPreferences.getFloat("yingzhishengao", 180)) - (4.7 * ComputeMyInfo
                    .getInstance().getBirthday());
            Log.d("koma", "基础代谢---" + a);
            return (float) a;
        }
    }

    public double computeQuzhitizhong() {
        double a = mSharedPreferences.getFloat("yingzhitizhong", 68) - (mSharedPreferences
                .getFloat("yingzhitizhong", 68) * computeTizhilv());
        Log.d("koma", "去脂体重---" + a);
        return a;
    }

    public double computeYaotunbi() {
        double a = mSharedPreferences.getFloat("yingzhiyaowei", 70) / mSharedPreferences.getFloat
                ("yingzhitunwei", 100) * 100;
        Log.d("koma", "腰臀比---" + a);
        return a;
    }

    public float computeShuifen() {
        double a = 1.369 + 0.597 * mSharedPreferences.getFloat("yingzhishengao", 180) *
                mSharedPreferences.getFloat("yingzhishengao", 180) / 290 + 0.099 *
                mSharedPreferences.getFloat("yingzhitizhong", 68) - 0.009 * ComputeMyInfo
                .getInstance().getBirthday();
        Log.d("koma", "水份---" + a);
        return (float) a;
    }

    public void initShuifenBi() {
        int age = ComputeMyInfo.getInstance().getBirthday();
        Log.d("koma", "年龄" + age);
        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            if (age <= 11)
                mShuifenBi = 60.0f;
            else if (age <= 16)
                mShuifenBi = 62.8f;
            else if (age <= 21)
                mShuifenBi = 64.2f;
            else if (age <= 26)
                mShuifenBi = 62.5f;
            else if (age <= 31)
                mShuifenBi = 59.4f;
            else if (age <= 36)
                mShuifenBi = 58.0f;
            else if (age <= 41)
                mShuifenBi = 57.2f;
            else if (age <= 46)
                mShuifenBi = 55.0f;
            else if (age <= 51)
                mShuifenBi = 56.6f;
            else if (age <= 56)
                mShuifenBi = 57.3f;
            else if (age <= 61)
                mShuifenBi = 59.2f;
            else if (age > 61)
                mShuifenBi = 56.9f;
        } else {
            if (age <= 11)
                mShuifenBi = 56.5f;
            else if (age <= 16)
                mShuifenBi = 55.1f;
            else if (age <= 21)
                mShuifenBi = 53.9f;
            else if (age <= 26)
                mShuifenBi = 53.4f;
            else if (age <= 31)
                mShuifenBi = 50.9f;
            else if (age <= 36)
                mShuifenBi = 49.8f;
            else if (age <= 41)
                mShuifenBi = 51.5f;
            else if (age <= 46)
                mShuifenBi = 49.1f;
            else if (age <= 51)
                mShuifenBi = 49.7f;
            else if (age <= 56)
                mShuifenBi = 48.4f;
            else if (age <= 61)
                mShuifenBi = 47.0f;
            else if (age > 61)
                mShuifenBi = 48.5f;
        }

    }


    private void initJichudaixieBi() {
        int age = ComputeMyInfo.getInstance().getBirthday();
        if (DataSupport.find(MyInfoBean.class, 1).getSex().equals("男")) {
            if (age <= 2) {
                mJichudaixieBi = 700;
            } else if (age <= 5) {
                mJichudaixieBi = 900;
            } else if (age <= 8) {
                mJichudaixieBi = 1090;
            } else if (age <= 11) {
                mJichudaixieBi = 1290;
            } else if (age <= 14) {
                mJichudaixieBi = 1480;
            } else if (age <= 17) {
                mJichudaixieBi = 1610;
            } else if (age <= 29) {
                mJichudaixieBi = 1550;
            } else if (age <= 49) {
                mJichudaixieBi = 1500;
            } else if (age <= 69) {
                mJichudaixieBi = 1350;
            } else if (age == 70)
                mJichudaixieBi = 1220;
        } else {
            if (age <= 2) {
                mJichudaixieBi = 700;
            } else if (age <= 5) {
                mJichudaixieBi = 860;
            } else if (age <= 8) {
                mJichudaixieBi = 1000;
            } else if (age <= 11) {
                mJichudaixieBi = 1180;
            } else if (age <= 14) {
                mJichudaixieBi = 1340;
            } else if (age <= 17) {
                mJichudaixieBi = 1300;
            } else if (age <= 29) {
                mJichudaixieBi = 1210;
            } else if (age <= 49) {
                mJichudaixieBi = 1170;
            } else if (age <= 69) {
                mJichudaixieBi = 1110;
            } else if (age == 70)
                mJichudaixieBi = 1010;
        }

    }
}
