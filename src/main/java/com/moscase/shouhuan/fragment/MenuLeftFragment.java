package com.moscase.shouhuan.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.BluetoothScanActivity;
import com.moscase.shouhuan.activity.CalendarActivity;
import com.moscase.shouhuan.activity.NotifyActivity;
import com.moscase.shouhuan.activity.PhotoActivity;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.PermissionUtil;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.inuker.bluetooth.library.Constants.REQUEST_FAILED;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.moscase.shouhuan.utils.MyApplication.encodeHexStr;


/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class MenuLeftFragment extends Fragment {
    String hexStr;
    private LinearLayout mCalendarLinearLayout;
    private LinearLayout mPhotoLinearLayout;
    private LinearLayout mNotifyLayout;
    private LinearLayout mExitLayout;
    private RelativeLayout mBlueTooth;
    private ImageView mEle;
    private ImageView mSign;

    private Runnable mRunnable;

    private BluetoothClient mBluetoothClient;
    private SharedPreferences mSharedPreferences;
    private String[] mBlueToothPermission = {Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.ACCESS_FINE_LOCATION
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getActivity(), "读到了电量", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu, container, false);


        initView(view);


        mCalendarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });


        mPhotoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                startActivity(intent);

            }
        });


        mNotifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotifyActivity.class);
                startActivity(intent);
            }
        });


        mBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    //6.0之后动态申请权限
                    showBluetoothPer();
                } else {
                    gotoBluetoothActivity();
                }

            }
        });

        mExitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExitLayout.setVisibility(View.GONE);
                mBluetoothClient.disconnect(mSharedPreferences.getString("mac", ""));
            }
        });


        return view;
    }

    private void initView(View view) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.chenhang.disconnect");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
        mSharedPreferences = getActivity().getSharedPreferences("ToggleButton", MODE_PRIVATE);
        mBluetoothClient = MyApplication.getBleManager();
        mCalendarLinearLayout = (LinearLayout) view.findViewById(R.id.calendar);
        mPhotoLinearLayout = (LinearLayout) view.findViewById(R.id.takePhoto);
        mNotifyLayout = (LinearLayout) view.findViewById(R.id.notify);
        mExitLayout = view.findViewById(R.id.exit);
        mBlueTooth = (RelativeLayout) view.findViewById(R.id.bluetooth);
        mEle = view.findViewById(R.id.ele);
        mSign = view.findViewById(R.id.sign);

//180f 2a19

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothClient.read(mSharedPreferences.getString("mac", ""), UUID.fromString
                                ("0000180f-0000-1000-8000-00805f9b34fb")

                        , UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"), new
                                BleReadResponse() {
                                    @Override
                                    public void onResponse(int code, byte[] bytes) {
                                        if (code == REQUEST_SUCCESS) {
                                            mExitLayout.setVisibility(View.VISIBLE);
                                            byte[] value = bytes;
                                            hexStr = encodeHexStr(value);
                                            //转换成十六进制
                                            int i = Integer.parseInt(hexStr, 16);
                                            if (i >= 22 && i < 38) {
                                                mEle.setImageResource(R.drawable.electric_three);
                                            } else if (i >= 38 && i < 54) {
                                                mEle.setImageResource(R.drawable.electric_four);
                                            } else if (i >= 54 && i < 70) {
                                                mEle.setImageResource(R.drawable.electric_five);
                                            } else if (i >= 70 && i < 85) {
                                                mEle.setImageResource(R.drawable.electric_six);
                                            } else if (i >= 85) {
                                                mEle.setImageResource(R.drawable.electric_seven);
                                            } else {
                                                mEle.setImageResource(R.drawable.electric_one);
                                            }
                                        }else if (code == REQUEST_FAILED)
                                            mExitLayout.setVisibility(View.GONE);
                                    }
                                });

                mBluetoothClient.readRssi(mSharedPreferences.getString("mac", ""), new
                        BleReadRssiResponse() {

                            @Override
                            public void onResponse(int i, Integer integer) {
                                if (i == REQUEST_SUCCESS) {
                                    mExitLayout.setVisibility(View.VISIBLE);
                                    if (i >= -55) {
                                        mSign.setImageResource(R.drawable.sign);
                                    } else if (i >= -65) {
                                        mSign.setImageResource(R.drawable.sign_six);
                                    } else if (i >= -75) {
                                        mSign.setImageResource(R.drawable.sign_five);
                                    } else if (i >= -80) {
                                        mSign.setImageResource(R.drawable.sign_four);
                                    } else if (i >= -90) {
                                        mSign.setImageResource(R.drawable.sign_three);
                                    } else {
                                        mSign.setImageResource(R.drawable.sign_two);
                                    }
                                }else if (i == REQUEST_FAILED)
                                    mExitLayout.setVisibility(View.GONE);
                            }
                        });

                mHandler.postDelayed(this, 10000);
            }
        };

        mHandler.postDelayed(mRunnable, 1000);


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


    public void gotoBluetoothActivity() {
        mBluetoothClient.openBluetooth();
        Intent intent = new Intent(getActivity(), BluetoothScanActivity.class);
        startActivity(intent);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.chenhang.disconnect")) {
                mEle.setImageResource(R.drawable.electric_one);
                mSign.setImageResource(R.drawable.sign_two);
            }else if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter
                        .STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {

                } else if (state == BluetoothAdapter.STATE_OFF) {
                    mExitLayout.setVisibility(View.GONE);
                }
            }
        }
    };


    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroyView();
    }
}
