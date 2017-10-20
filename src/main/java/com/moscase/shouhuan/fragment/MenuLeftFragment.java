package com.moscase.shouhuan.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.BluetoothScanActivity;
import com.moscase.shouhuan.activity.CalendarActivity;
import com.moscase.shouhuan.activity.NotifyActivity;
import com.moscase.shouhuan.activity.PhotoActivity;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.PermissionUtil;

/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
public class MenuLeftFragment extends Fragment {
    private LinearLayout mCalendarLinearLayout;
    private LinearLayout mPhotoLinearLayout;
    private LinearLayout mNotifyLayout;
    private RelativeLayout mBlueTooth;

    private String[] mBlueToothPermission = {Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.ACCESS_FINE_LOCATION
    };

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

        mPhotoLinearLayout = (LinearLayout) view.findViewById(R.id.takePhoto);
        mPhotoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                startActivity(intent);

            }
        });

        mNotifyLayout = (LinearLayout) view.findViewById(R.id.notify);
        mNotifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotifyActivity.class);
                startActivity(intent);
            }
        });

        mBlueTooth = (RelativeLayout) view.findViewById(R.id.bluetooth);
        mBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    //6.0之后动态申请权限
                    showBluetoothPer();
                }else {
                    gotoBluetoothActivity();
                }

            }
        });

        return view;
    }

    private void showBluetoothPer() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            requestBluetoothPermissions();
        } else {
           gotoBluetoothActivity();
        }
    }

    private void requestBluetoothPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.BLUETOOTH_ADMIN)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.BLUETOOTH)
                ) {
            ActivityCompat
                    .requestPermissions(getActivity(), mBlueToothPermission,
                            123);
        } else {
            ActivityCompat.requestPermissions(getActivity(), mBlueToothPermission, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[]
                                                   grantResults) {
        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                gotoBluetoothActivity();
            } else {
                Toast.makeText(getActivity(), "请在设置中授予权限", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void gotoBluetoothActivity(){
        MyApplication.getBleManager().enableBluetooth();
        Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.getBleManager().isConnected()){

        }
    }
}
