package com.moscase.shouhuan.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;

import org.greenrobot.eventbus.EventBus;

public class ConnectService extends Service {
    public ConnectService() {
    }
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScanResult scanResult = intent.getParcelableExtra("scanresult");
        Log.d("koma","service启动");
        connect(scanResult);

        return super.onStartCommand(intent, flags, startId);
    }

    private void connect(ScanResult scanResult) {
        MyApplication.getBleManager().connectDevice(scanResult, false, new BleGattCallback() {
            @Override
            public void onConnectError(BleException exception) {
                Log.d("koma","连接失败");
                Intent intent = new Intent();
                intent.setAction("lianjieshibai");
                sendBroadcast(intent);

            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                Log.d("koma","连接成功");
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectService.this, "连接成功", Toast.LENGTH_SHORT).show();
                        mSharedPreferences.edit().putBoolean("isconnected",true).commit();
                    }
                });
                Intent intent = new Intent();
                intent.setAction("lianjiechenggong");
                sendBroadcast(intent);

            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {

                final String mac = mSharedPreferences.getString("mac", "");
                Log.d("koma", "断开连接!!!!!!!!!!!!!!!!!");

                mSharedPreferences.edit().putBoolean("isconnected",false).commit();
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectService.this, "断开连接", Toast.LENGTH_LONG).show();
                        MyApplication.getBleManager().scanDevice(new ListScanCallback(10000*1000) {
                            @Override
                            public void onScanning(ScanResult result) {
                                if (result.getDevice().getAddress().equals(mac)) {
                                    Log.d("koma","正在重新连接"+result.getDevice().getName());
                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ConnectService.this, "正在重新连接...", Toast
                                                    .LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    connect(result);
                                }
                            }

                            @Override
                            public void onScanComplete(ScanResult[] results) {

                            }
                        });
                    }
                },1000);
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {
                Log.d("koma","连接ing");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.d("koma","发现服务");

                //发现服务0.5秒后订阅notify
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("koma","发送消息");
                        EventBus.getDefault().post(new MessageEvent(123));

                    }
                }, 500);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }
}
