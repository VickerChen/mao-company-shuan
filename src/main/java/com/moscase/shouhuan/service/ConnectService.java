package com.moscase.shouhuan.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;

import org.greenrobot.eventbus.EventBus;

import static com.inuker.bluetooth.library.Constants.REQUEST_FAILED;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;
import static com.moscase.shouhuan.utils.MyApplication.isFirstChanglianjie;

public class ConnectService extends Service {
    public ConnectService() {
    }

    private SharedPreferences mSharedPreferences;
    private BluetoothClient mBluetoothClient;
    private Runnable mRunnable;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        mBluetoothClient = MyApplication.getBleManager();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int status = mBluetoothClient.getConnectStatus(mSharedPreferences.getString("mac", ""));
                if (status == Constants.STATUS_DEVICE_DISCONNECTED)

                mHandler.postDelayed(this, 1000);
            }
        };
//        mHandler.post(mRunnable);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d("koma", "service启动");
            connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void connect() {

        mBluetoothClient.connect(mSharedPreferences.getString("mac", ""), new BleConnectResponse() {
            @Override
            public void onResponse(int i, BleGattProfile bleGattProfile) {
                if (i == REQUEST_SUCCESS) {
                    mBluetoothClient.registerConnectStatusListener(mSharedPreferences.getString("mac", ""), mBleConnectStatusListener);
                    Log.d("koma", "连接成功");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConnectService.this, "连接成功", Toast.LENGTH_SHORT).show();
                            mSharedPreferences.edit().putBoolean("isconnected", true).commit();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setAction("lianjiechenggong");
                    sendBroadcast(intent);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("koma", "发送消息");
                            EventBus.getDefault().post(new MessageEvent(123));

                        }
                    }, 500);
                }else if (i == REQUEST_FAILED){
                    Intent intent = new Intent();
                    intent.setAction("lianjieshibai");
                    sendBroadcast(intent);
                }
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

    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {

            } else if (status == STATUS_DISCONNECTED) {
                Log.d("koma", "断开连接!!!!!!!!!!!!!!!!!");
                isFirstChanglianjie = true;
                Intent intent = new Intent();
                intent.setAction("com.chenhang.disconnect");
                sendBroadcast(intent);
                mSharedPreferences.edit().putBoolean("isconnected", false).commit();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reconnect();
                    }
                },1000);

            }
        }
    };

    private void reconnect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                SearchRequest request = new SearchRequest.Builder()
                        .searchBluetoothLeDevice(3000, 1000*3)   // 先扫BLE设备3次，每次3s
                        .build();

                mBluetoothClient.search(request, new SearchResponse() {
                    @Override
                    public void onSearchStarted() {

                    }

                    @Override
                    public void onDeviceFounded(SearchResult searchResult) {
                        if (searchResult.getAddress().equals(mSharedPreferences.getString("mac",""))) {
                            Log.d("koma", "正在重新连接" + searchResult.getName());
                            //我忘了这里是不是主线程了
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connect();
                                }
                            },1000);
                        }
                    }

                    @Override
                    public void onSearchStopped() {
                        reconnect();
                    }

                    @Override
                    public void onSearchCanceled() {

                    }
                });
            }
        }, 1000);
    }
}
