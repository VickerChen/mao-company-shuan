package com.moscase.shouhuan.fragment;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.PermissionUtil;
import com.moscase.shouhuan.view.CircleView;
import com.moscase.shouhuan.view.RingView;

import java.text.NumberFormat;

import static android.content.Context.MODE_PRIVATE;
import static com.moscase.shouhuan.utils.MyApplication.isResume;

//import com.moscase.shouhuan.activity.DistanceActivity;

/**
 * Created by 陈航 on 2017/8/25.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class ZhuangtaiFragment extends Fragment {
    private RingView mRingView;
    private CircleView mCircleView;
    private TextView mMubiao;
    private TextView mBushu;
    private TextView mZongjuli;
    private TextView mKaluli;
    private TextView mMoxige;
    private TextView mBuchangCM;
    private TextView mBuchang;
    private TextView mTv_Time;
    private TextView mTv_bupin;
    private int lastBushu;
    private int mubiao;
    private SharedPreferences mSharedPreferences;
    //这是为了让步数显示动画的判断
    private int length;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public ZhuangtaiFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuangtai1, container, false);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.chenhang.inch");
        filter.addAction("com.chenhang.time");
        filter.setPriority(Integer.MAX_VALUE);
        getActivity().registerReceiver(myReceiver, filter);
        mSharedPreferences = getActivity().getSharedPreferences("myInfo", MODE_PRIVATE);
        initView(view);
        return view;
    }


    private void initView(View view) {
        mBuchang = (TextView) view.findViewById(R.id.buchang);
        mBuchangCM = (TextView) view.findViewById(R.id.buchangcm);
        if (MyApplication.isInch)
            mBuchangCM.setText("inch");
        mTv_Time = view.findViewById(R.id.tv_time);
        mTv_bupin = view.findViewById(R.id.tv_bupin);
        mZongjuli = (TextView) view.findViewById(R.id.zongjuli);
        mKaluli = (TextView) view.findViewById(R.id.kaluli);
        mMubiao = (TextView) view.findViewById(R.id.mubiaobushu);
        mMoxige = (TextView) view.findViewById(R.id.moxige);
        mBushu = view.findViewById(R.id.bushu);
        mRingView = (RingView) view.findViewById(R.id.MiClockView);
        mCircleView = (CircleView) view.findViewById(R.id.circleview);
//        mRingView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mRingView.getIsAnimRunning()) {
//                    mRingView.startAnim();
//                    mCircleView.startAnim();
//                }
//            }
//        });

    }


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
//            init();
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

            ActivityCompat
                    .requestPermissions(getActivity(), permissions,
                            123);

        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
//                init();
            } else {
                Toast.makeText(getActivity(), "请授予权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "请授予权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAngel(final int result, float zongjuli, float kaluli) {
        Log.d("result", "resultis" + result + "总记录is" + zongjuli + "卡路里is" + kaluli);

        if (zongjuli == 0) {
            mZongjuli.setText("0.00");
        } else {
            mZongjuli.setText(zongjuli + "");
        }

        if (kaluli == 0) {
            mKaluli.setText("0.0");
        } else {
//            DecimalFormat df = new DecimalFormat("#,##0.0");
            mKaluli.setText(String.format("%.1f", kaluli*1000));
        }

        int temp = Math.round(kaluli * 1000 / 307);
        Log.d("moxige", temp + "");
        mMoxige.setText("≈" + temp + "个墨西哥鸡肉卷的热量");

        //当这次拿到的步数和上一次的步数不一样的时候才有动画
        if (lastBushu != result)
            mBushu.setText("" + result);


//            NumAnim.startAnim(mBushu, result);
        lastBushu = result;
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后4位
        numberFormat.setMaximumFractionDigits(6);
        double baifenbi = Double.parseDouble(numberFormat.format((float) result / (float) mubiao));

        if (isResume) {
            mRingView.setAngel(baifenbi);
            mCircleView.setAngel(baifenbi);
//            mRingView.performClick();
        }
    }

    public void setInch() {
        if (MyApplication.isInch) {
            mBuchangCM.setText("inch");
            mBuchang.setText("" + mSharedPreferences.getInt("buchangft", 30));
        } else {
            mBuchang.setText("" + mSharedPreferences.getInt("buchang", 75));
            mBuchangCM.setText("CM");
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.chenhang.inch")) {
                Log.d("koma---修改公英制", MyApplication.isInch + "");
                mSharedPreferences.edit().clear().commit();
                setInch();
            } else if (intent.getAction().equals("com.chenhang.time")) {
                String time = intent.getStringExtra("time");
                String h = time.substring(0, 2);
                String s = time.substring(4, 6);
                String m = time.substring(2, 4);
                mTv_Time.setText(h + "H " + m + "M " + s + "S");
                int step = intent.getIntExtra("step", 0);
                int ih = Integer.parseInt(h);
                int im = Integer.parseInt(m);
                int is = Integer.parseInt(s);
                float second = 0;
                if (ih != 0)
                    second = ih * 60 * 60;
                if (im != 0)
                    second = second + im * 60;
                if (is != 0) {
                    second = second + is;
                    if (second == 0)
                        mTv_bupin.setText(0 + "");
                    else
                        mTv_bupin.setText(Math.round(step / (second / 60.0f)) + "");
                }

            }
        }
    };

    @Override
    public void onResume() {
        mubiao = mSharedPreferences.getInt("mubiao", 10000);
        mMubiao.setText(mubiao + "");
        setInch();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(myReceiver);
        super.onDestroyView();
    }
}
