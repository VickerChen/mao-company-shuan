package com.moscase.shouhuan.fragment;


import android.Manifest;
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
import com.moscase.shouhuan.utils.PermissionUtil;
import com.moscase.shouhuan.view.CircleView;
import com.moscase.shouhuan.view.NumAnim;
import com.moscase.shouhuan.view.RingView;

import java.text.NumberFormat;

import static com.moscase.shouhuan.utils.MyApplication.isResume;

//import com.moscase.shouhuan.activity.DistanceActivity;

/**
 * Created by 陈航 on 2017/8/25.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
public class ZhuangtaiFragment extends Fragment {
    private RingView mRingView;
    private CircleView mCircleView;
    private TextView mMubiao;
    private TextView mBushu;
    private TextView mZongjuli;
    private TextView mKaluli;
    private TextView mMoxige;
    private int lastBushu;
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
        initView(view);
        return view;
    }


    private void initView(View view) {
        mZongjuli = (TextView) view.findViewById(R.id.zongjuli);
        mKaluli = (TextView) view.findViewById(R.id.kaluli);
        mMubiao = (TextView) view.findViewById(R.id.mubiaobushu);
        mMubiao.setText("" + 10000);
        mMoxige = (TextView) view.findViewById(R.id.moxige);
        mBushu = (TextView) view.findViewById(R.id.bushu);
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

    public void setAngel(int result,float zongjuli,float kaluli) {
        Log.d("resultis", result + "");

        mZongjuli.setText(zongjuli+"");
        mKaluli.setText(kaluli+"");
        int temp = (int) (kaluli/307);
        Log.d("moxige",temp+"");
        mMoxige.setText("≈"+temp+"个墨西哥鸡肉卷的热量");

        //当这次拿到的步数和上一次的步数不一样的时候才有动画
        if (lastBushu != result)
            NumAnim.startAnim(mBushu, result);
        lastBushu = result;
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后4位
        numberFormat.setMaximumFractionDigits(6);
        double baifenbi = Double.parseDouble(numberFormat.format((float) result / (float) 10000));
        Log.d("koma---百分比是", baifenbi + "");
        if (isResume) {
            mRingView.setAngel(baifenbi);
            mCircleView.setAngel(baifenbi);
//            mRingView.performClick();
        }
    }
}
