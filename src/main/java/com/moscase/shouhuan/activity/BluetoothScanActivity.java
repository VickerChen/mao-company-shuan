package com.moscase.shouhuan.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.service.BluetoothService;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 这是蓝牙扫描结果的界面
 */
public class BluetoothScanActivity extends AppCompatActivity {

    private MyAdapter mMyAdapter;
    private BluetoothService mBluetoothService;
    private List<ScanResult> scanCompareList = new ArrayList<>();
    private List<String> scanCompareListAddress = new ArrayList<>();
    private List<String> scanListAddress = new ArrayList<>();
    private ListView mListView;
    private String mCurrentConnectedBlueName;

    //连接时的dialog
    private ProgressDialog mConnectingDialog;
    //扫描时的dialog
    private ProgressDialog mScaningDialog;
    //右上角刷新按钮
    private ImageButton mRefreshButton;

    private boolean isScanOver = false;

    private MyReceiver mReceiver;
    private Handler mHandler;

    //这本来是存储长短连接的check值的，我拿来顺便存储一个mac地址
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        mHandler = new Handler();
        mRefreshButton = (ImageButton) findViewById(R.id.btn_refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
//                    mMyAdapter.clear();
//                    scanCompareList.clear();
                    mBluetoothService.scanDevice();
                    mScaningDialog.setMessage("搜索中...");
                    mScaningDialog.show();
                }
            }
        });

        mScaningDialog = new ProgressDialog(this);
        mScaningDialog.setCanceledOnTouchOutside(false);
        mScaningDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mBluetoothService.cancelScan();
            }
        });
//        从leftFragment跳过来的时候顺便开启蓝牙，开启蓝牙需要一段时间，在下面进行判断
//        如果蓝牙是开启状态，直接开始扫描，如果没有开启，利用广播等待这一段时间，
//        如果不等待，蓝牙开启后不会自动扫描，需要退出重新进入这个界面才会扫描
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(mReceiver, filter);

        initView();

        if (MyApplication.getBleManager().isBlueEnable()) {
            mScaningDialog.setMessage("搜索中...");
            mScaningDialog.show();
            if (mBluetoothService == null) {
                bindService();
            } else {
                mBluetoothService.scanDevice();
            }
        }
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
                finish();
            }
        });


        mListView = (ListView) findViewById(R.id.list_device);
        mMyAdapter = new MyAdapter(this);
        mListView.setAdapter(mMyAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mBluetoothService != null) {
                    mBluetoothService.connectDevice(mMyAdapter.getItem(i));
                    String mac = mMyAdapter.getItem(i).getDevice().getAddress();
                    mSharedPreferences.edit().putString("mac", mac).commit();
                    mConnectingDialog = new ProgressDialog(BluetoothScanActivity.this);
                    mConnectingDialog.setMessage("正在连接...");
                    mConnectingDialog.show();
                }
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
                    if (mBluetoothService == null) {
                        bindService();
                    } else {
                        mBluetoothService.scanDevice();
                    }
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    mMyAdapter.clear();
                    scanCompareList.clear();
                    scanCompareListAddress.clear();
                    mMyAdapter.notifyDataSetChanged();
                }

            }

        }

    }

    /**
     * ListView的适配器
     */
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<ScanResult> scanResultList;

        MyAdapter(Context context) {
            this.context = context;
            scanResultList = new ArrayList<>();
        }

        void addResult(ScanResult result) {
            scanResultList.add(result);
        }

        void clear() {
            scanResultList.clear();
        }

        void compare(List<ScanResult> list) {
            for (int i = 0; i < scanResultList.size(); i++) {
                if (!scanCompareListAddress.contains(mMyAdapter.getItem(i).getDevice().getAddress
                        ())) {
                    Log.d("koma", "删除了一个");
                    scanListAddress.remove(mMyAdapter.getItem(i).getDevice().getAddress());
                    mMyAdapter.removeItem(mMyAdapter.getItem(i).getDevice().getName());
                    mMyAdapter.notifyDataSetChanged();
                }
            }
//            for (ScanResult scanResult : scanResultList) {
//                if (!list.contains(scanResult)) {
//                    mMyAdapter.removeItem(scanResult.getDevice().getName());
//                    mMyAdapter.notifyDataSetChanged();
//                }
//            }
        }

        List<ScanResult> getList() {
            return scanResultList;
        }

        void removeItem(String name) {
            ScanResult result;
//            BluetoothDevice device = result.getDevice();
//            String name = device.getName();
            for (int i = 0; i < scanResultList.size(); i++) {
                BluetoothDevice device = mMyAdapter.getItem(i).getDevice();
                String name1 = device.getName();
                if (name1.equals(name)) {
                    scanResultList.remove(i);
                }
            }
//            for (ScanResult scanResult : scanResultList) {
//                result = scanResult;
//                BluetoothDevice device = result.getDevice();
//                String name1 = device.getName();
//                if (name1.equals(name)) {
//                    scanResultList.remove(scanResult);
//                }
//            }
        }

        @Override
        public int getCount() {
            return scanResultList.size();
        }

        @Override
        public ScanResult getItem(int position) {
            if (position > scanResultList.size())
                return null;
            return scanResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAdapter.ViewHolder holder;
            if (convertView != null) {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.device_item_layout, null);
                holder = new MyAdapter.ViewHolder();
                convertView.setTag(holder);
                holder.txt_name = (TextView) convertView.findViewById(R.id.device_name);
                holder.txt_mac = (TextView) convertView.findViewById(R.id.ble_states);
            }

            ScanResult result = scanResultList.get(position);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            String mac = device.getAddress();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            return convertView;
        }

        class ViewHolder {
            TextView txt_name;
            TextView txt_mac;
        }
    }


    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
            mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }

    };

    /**
     * 扫描的各种回调方法
     */
    private BluetoothService.Callback callback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {
//            mMyAdapter.clear();
//            scanCompareList.clear();
            mMyAdapter.notifyDataSetChanged();
            Log.d("koma", "开始扫描");
        }

        @Override
        public void onScanning(final ScanResult result) {
            BluetoothDevice device = result.getDevice();
            final String name = device.getName();
            if (name != null && !name.equals("")) {
                mScaningDialog.dismiss();
//                scanCompareList.add(result);
                if (!scanListAddress.contains(result.getDevice().getAddress())) {
                    mMyAdapter.addResult(result);
                    scanListAddress.add(result.getDevice().getAddress());
                    Log.d("koma", "要展示的添加了一个" + result.getDevice().getName());
                }

                Log.d("koma", "添加了一个");
                scanCompareListAddress.add(result.getDevice().getAddress());
                scanCompareList.add(result);


                mMyAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScanComplete() {
            if (mScaningDialog.isShowing())
                mScaningDialog.dismiss();
//            if (mMyAdapter.getCount() == 0) {
//                Toast.makeText(BluetoothScanActivity.this, "未发现可用设备,请重试", Toast.LENGTH_SHORT)
//                        .show();
//            } else {
//                Toast.makeText(BluetoothScanActivity.this, "扫描完毕", Toast.LENGTH_SHORT).show();
//            }
            isScanOver = true;

            mMyAdapter.compare(scanCompareList);
            Log.d("koma", "扫描完毕");


            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothService.scanDevice();
                    scanCompareList.clear();
                    scanCompareListAddress.clear();
                }
            }, 300);
        }

        @Override
        public void onConnecting() {
            Log.d("koma", "连接中");
        }

        @Override
        public void onConnectFail() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mBluetoothService, "连接失败", Toast.LENGTH_SHORT).show();
                    if (mConnectingDialog != null)
                        mConnectingDialog.dismiss();
                }
            });
            Log.e("koma", "连接失败");
        }

        @Override
        public void onDisConnected(BleException exp) {
            Toast.makeText(mBluetoothService, "断开连接", Toast.LENGTH_SHORT).show();
            mMyAdapter.removeItem(mCurrentConnectedBlueName);
            mMyAdapter.notifyDataSetChanged();
            if (mConnectingDialog != null)
                mConnectingDialog.dismiss();
            Log.e("koma", "断开连接" + exp);
        }

        @Override
        public void onServicesDiscovered() {
            Log.d("koma", "发现服务");
            mConnectingDialog.dismiss();
            Toast.makeText(mBluetoothService, "连接成功", Toast.LENGTH_SHORT).show();
            Log.d("koma", "连接成功");

            //发现服务0.5秒后订阅notify
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    EventBus.getDefault().post(new MessageEvent(123));

//                    if (!isEnabled()) {
//                        startActivity(new Intent("android.settings" +
//                                ".ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//                    } else {
//                        Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast
//                                .LENGTH_SHORT);
//                        toast.show();
//                    }
                }
            }, 500);

        }

        @Override
        public void onConnectSuccess() {


        }
    };


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
    protected void onDestroy() {
        mBluetoothService.cancelScan();
        mMyAdapter.clear();
        scanCompareList.clear();
        unregisterReceiver(mReceiver);
        mHandler.removeCallbacksAndMessages(null);
        unbindService();
        super.onDestroy();
    }

}
