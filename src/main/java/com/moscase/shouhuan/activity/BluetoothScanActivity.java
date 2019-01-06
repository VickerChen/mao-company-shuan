package com.moscase.shouhuan.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.service.MyBleService;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.view.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 这是蓝牙扫描结果的界面
 */
public class BluetoothScanActivity extends AppCompatActivity {
    private List<BluetoothDevice> scanResultList;
    private Adapter mMyAdapter;
    //    private BluetoothService mBluetoothService;
    private List<BluetoothDevice> scanCompareList = new ArrayList<>();
    private List<String> scanCompareListAddress = new ArrayList<>();
    private List<String> scanListAddress = new ArrayList<>();
    //    private ListView mListView;
    private RecyclerView mListView;
    private TextView mCurrentConnectedName;
    private RelativeLayout mRl_name;

    //连接时的dialog
    private ProgressDialog mConnectingDialog;
    //扫描时的dialog
    private ProgressDialog mScaningDialog;
    //右上角刷新按钮
    private ImageButton mRefreshButton;

    private MyReceiver mReceiver;

    //这本来是存储长短连接的check值的，我拿来顺便存储一个mac地址
    private SharedPreferences mSharedPreferences;


    private boolean isAutoCancleBle = true;


    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;
    // stop scan after 10 second
    private static final long SCAN_PERIOD = 10000;


    private MyBleService mMyBleService;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback
            () {

        @Override
        public void onLeScan(final BluetoothDevice bleDevice, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // 扫描到一个符合扫描规则的BLE设备（主线程）
                    Log.d("koma", "扫描到设备" + bleDevice.getName());
                    final String name = bleDevice.getName();
                    if (!(name == null) && !name.equals("") && !name.equals("null") && !name.equals
                            ("NULL")) {
                        mScaningDialog.dismiss();
                        if (!scanListAddress.contains(bleDevice.getAddress())) {

                            mMyAdapter.addResult(bleDevice);
                            scanListAddress.add(bleDevice.getAddress());
                            Log.d("koma3", "添加了" + bleDevice.getName());
                        }

                        scanCompareListAddress.add(bleDevice.getAddress());
                        scanCompareList.add(bleDevice);


                        mMyAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d("koma---", "扫描了");
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(2);
                    }
                }, 3000);
            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mMyAdapter.compare();
                if (mScaningDialog.isShowing())
                    mScaningDialog.dismiss();

                scanCompareList.clear();
                scanCompareListAddress.clear();
                mHandler.sendEmptyMessage(1);
            }
            super.handleMessage(msg);
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mMyBleService = ((MyBleService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context
                .BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mHandler.sendEmptyMessage(1);

        Intent intent = new Intent(this, MyBleService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);


        scanResultList = new ArrayList<>();
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        mRefreshButton = (ImageButton) findViewById(R.id.btn_refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyAdapter.clear();
                scanCompareList.clear();
                scanCompareListAddress.clear();
                scanListAddress.clear();
                scanResultList.clear();
                mMyAdapter.notifyDataSetChanged();
                mHandler.removeMessages(1);
                mHandler.removeMessages(2);
                mHandler.removeCallbacksAndMessages(null);
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mHandler.sendEmptyMessage(1);
            }
        });

        mScaningDialog = new ProgressDialog(this);
        mScaningDialog.setCanceledOnTouchOutside(false);
//        mScaningDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                mBluetoothService.cancelScan();
//            }
//        });
//        从leftFragment跳过来的时候顺便开启蓝牙，开启蓝牙需要一段时间，在下面进行判断
//        如果蓝牙是开启状态，直接开始扫描，如果没有开启，利用广播等待这一段时间，
//        如果不等待，蓝牙开启后不会自动扫描，需要退出重新进入这个界面才会扫描
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("lianjieshibai");
        filter.addAction("lianjiechenggong");
        filter.addAction("com.chenhang.disconnect");
        registerReceiver(mReceiver, filter);

        initView();

        mScaningDialog.setMessage("搜索中...");
        mScaningDialog.show();


    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("搜索设备");
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.refresh));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutoCancleBle = false;
                finish();
            }
        });
        mCurrentConnectedName = (TextView) findViewById(R.id.connectedname);
        mRl_name = (RelativeLayout) findViewById(R.id.rl_name);


        mListView = (RecyclerView) findViewById(R.id.list_device);
        mMyAdapter = new Adapter(R.layout.device_item_layout, scanResultList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mHandler.removeMessages(1);
                mHandler.removeMessages(2);
                mHandler.removeCallbacksAndMessages(null);
                Log.d("koma---", mMyAdapter.getItem(i).getName().contains("SWBLE") + "");
//                if (mMyAdapter.getItem(i).getName().contains("SWBLE")) {
                String mac = mMyAdapter.getItem(i).getAddress();
                MyApplication.sBluetoothDevice = mMyAdapter.getItem(i);
                mMyBleService.connect(mac);

                String currentname = mMyAdapter.getItem(i).getName();
                mSharedPreferences.edit().putString("mac", mac).commit();
                mSharedPreferences.edit().putString("currentname", currentname).commit();
                mConnectingDialog = new ProgressDialog(BluetoothScanActivity.this);
                mConnectingDialog.setCanceledOnTouchOutside(true);
                mConnectingDialog.setCancelable(true);
                mConnectingDialog.setMessage("正在连接...");
                mConnectingDialog.show();

//                Intent intent = new Intent(BluetoothScanActivity.this, ConnectService.class);
//                startService(intent);

//                } else {
//                    // 创建构建器
//                    AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothScanActivity
//                            .this);
//                    // 设置参数
//                    builder.setMessage("请选择正确的蓝牙手表").setPositiveButton("确定", new DialogInterface
//                            .OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//                    builder.create().show();
//                }
            }
        });
    }


    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter
                        .STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    mScaningDialog.setMessage("搜索中...");
                    mScaningDialog.show();
                    Log.e("koma", "打开了蓝牙");
//                    initScan();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mScaningDialog.isShowing())
                                mScaningDialog.cancel();
                        }
                    }, 5000);
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    mMyAdapter.clear();
                    scanCompareList.clear();
                    scanCompareListAddress.clear();
                    mMyAdapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals("lianjieshibai")) {
//                Toast.makeText(BluetoothScanActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                if (mConnectingDialog != null)
//                    mConnectingDialog.dismiss();
            } else if (intent.getAction().equals("lianjiechenggong")) {
                if (mConnectingDialog != null)
                    mConnectingDialog.dismiss();
//                Toast.makeText(BluetoothScanActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                mRl_name.setVisibility(View.VISIBLE);
                mCurrentConnectedName.setText(mSharedPreferences.getString("currentname", ""));
                mMyAdapter.removeItem("SWBLE");
                mMyAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals("com.chenhang.disconnect")) {
//                Toast.makeText(context, "断开连接", Toast.LENGTH_SHORT).show();
                mRl_name.setVisibility(View.GONE);
            }

        }

    }


    public class Adapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {
        void addResult(BluetoothDevice result) {
            scanResultList.add(result);
            notifyDataSetChanged();
        }

        void clear() {
            scanResultList.clear();
        }

        void compare() {
            for (int i = 0; i < scanResultList.size(); i++) {
                if (!scanCompareListAddress.contains(mMyAdapter.getItem(i).getAddress())) {
                    Log.d("koma", "删除了一个");
                    scanListAddress.remove(mMyAdapter.getItem(i).getAddress());
                    mMyAdapter.removeItem(mMyAdapter.getItem(i).getName());
                    mMyAdapter.notifyDataSetChanged();
                }
            }
        }

        List<BluetoothDevice> getList() {
            return scanResultList;
        }

        void removeItem(String name) {
            BluetoothDevice result;
            for (int i = 0; i < scanResultList.size(); i++) {
                String name1 = mMyAdapter.getItem(i).getName();
                if (name1.contains(name)) {
                    scanResultList.remove(i);
                }
            }
        }

        public Adapter(int layoutResId, @Nullable List<BluetoothDevice> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, BluetoothDevice bleDevice) {
            baseViewHolder.setText(R.id.device_name, bleDevice.getName());
            baseViewHolder.setText(R.id.ble_states, bleDevice.getAddress());
        }
    }

    private String getDate() {
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String date1 = dateFormat.format(date);
        date1 += "000730007f";
        return date1;
    }

    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        if (mSharedPreferences.getBoolean("isconnected", false)) {
            mRl_name.setVisibility(View.VISIBLE);
            mCurrentConnectedName.setText(mSharedPreferences.getString("currentname", ""));
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMyAdapter.clear();
        scanCompareList.clear();
        unregisterReceiver(mReceiver);
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        mHandler.removeCallbacksAndMessages(null);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        unbindService(conn);
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isAutoCancleBle = false;
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}