package com.moscase.shouhuan.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.conn.BleRssiCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.clj.fastble.utils.HexUtil;
import com.moscase.shouhuan.utils.MyApplication;

public class BluetoothService extends Service {
    public BluetoothBinder mBinder = new BluetoothBinder();
    private static BleManager bleManager;
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private Callback mCallback = null;
    private Callback2 mCallback2 = null;

    private String name;
    private String mac;
    private BluetoothGatt gatt;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        bleManager = MyApplication.getBleManager();
        bleManager.enableBluetooth();
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        bleManager = null;
        mCallback = null;
        mCallback2 = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        bleManager.closeBluetoothGatt();
        return super.onUnbind(intent);
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public void setScanCallback(Callback callback) {
        mCallback = callback;
    }

    public void setConnectCallback(Callback2 callback) {
        mCallback2 = callback;
    }

    public interface Callback {

        void onStartScan();

        void onScanning(ScanResult scanResult);

        void onScanComplete();

        void onConnecting();

        void onConnectFail();

        void onDisConnected(BleException exp);

        void onServicesDiscovered();

        void onConnectSuccess();
    }

    public interface Callback2 {

        void onDisConnected(BleException exp);
    }

    public void scanDevice() {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }


        boolean b = bleManager.scanDevice(new ListScanCallback(3000) {

            @Override
            public void onScanning(final ScanResult result) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanning(result);
                        }
                    }
                });
            }

            @Override
            public void onScanComplete(final ScanResult[] results) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
            }
        });
        if (!b) {
            if (mCallback != null) {
                mCallback.onScanComplete();
            }
        }
    }

    public void cancelScan() {
        bleManager.cancelScan();
    }

    public void connectDevice(final ScanResult scanResult) {
        if (mCallback != null) {
            mCallback.onConnecting();
        }

        bleManager.connectDevice(scanResult, true, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();

            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                mCallback.onConnectSuccess();
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putBoolean("isconnected",true).commit();
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }

            @Override
            public void onDisConnected(final BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        final String mac = mSharedPreferences.getString("mac", "");
                        Log.d("koma", "断开连接!!!!!!!!!!!!!!!!!");
                        mSharedPreferences.edit().putBoolean("isconnected",false).commit();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyApplication.getBleManager().scanDevice(new ListScanCallback(10000*1000) {
                                    @Override
                                    public void onScanning(ScanResult result) {
                                        if (result.getDevice().getAddress().equals(mac)) {
                                            Toast.makeText(BluetoothService.this, "正在重新连接", Toast
                                                    .LENGTH_SHORT).show();
                                            bleManager.connectDevice(result, true, new BleGattCallback() {
                                                @Override
                                                public void onConnectError(BleException exception) {

                                                }

                                                @Override
                                                public void onConnectSuccess(BluetoothGatt gatt, int
                                                        status) {

                                                }

                                                @Override
                                                public void onDisConnected(BluetoothGatt gatt, int
                                                        status, BleException exception) {

                                                }

                                                @Override
                                                public void onServicesDiscovered(BluetoothGatt gatt, int
                                                        status) {
                                                    Log.d("koma---", "已重新连接");
                                                    Toast.makeText(BluetoothService.this, "已重新连接", Toast
                                                            .LENGTH_SHORT).show();
                                                    mSharedPreferences.edit().putBoolean("isconnected",true).commit();

//                                            //发现服务0.5秒后订阅notify
//                                            new Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    EventBus.getDefault().post(new MessageEvent
//                                                            (123));
//                                                }
//                                            }, 500);
                                                    sendBroadcast(new Intent("com.chenhang.reconnect"));
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onScanComplete(ScanResult[] results) {

                                    }
                                });
                            }
                        },1000);

                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);
                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }

        });
    }

    public void scanAndConnect1(String name) {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }

        bleManager.scanNameAndConnect(name, 5000, false, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnecting();
                        }
                    }
                });
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("koma", "断开连接");
                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);

                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }
        });
    }

    public void scanAndConnect2(String name) {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }

        bleManager.scanfuzzyNameAndConnect(name, 5000, false, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnecting();
                        }
                    }
                });
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("koma", "断开连接");
                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);

                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }
        });
    }

    public void scanAndConnect3(String[] names) {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }

        bleManager.scanNamesAndConnect(names, 5000, false, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnecting();
                        }
                    }
                });
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("koma", "断开连接");
                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);

                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }
        });

    }

    public void scanAndConnect4(String[] names) {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }

        bleManager.scanfuzzyNamesAndConnect(names, 5000, false, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnecting();
                        }
                    }
                });
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("koma", "断开连接");
                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);

                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }
        });
    }

    public void scanAndConnect5(String mac) {
        resetInfo();

        if (mCallback != null) {
            mCallback.onStartScan();
        }

        bleManager.scanMacAndConnect(mac, 5000, false, new BleGattCallback() {

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onScanComplete();
                        }
                    }
                });
                BluetoothService.this.name = scanResult.getDevice().getName();
                BluetoothService.this.mac = scanResult.getDevice().getAddress();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnecting();
                        }
                    }
                });
            }

            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onConnectError(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, final BleException
                    exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("koma", "断开连接");
                        if (mCallback != null) {
                            mCallback.onDisConnected(exception);

                        }
                        if (mCallback2 != null) {
                            mCallback2.onDisConnected(exception);
                        }
                    }
                });
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onServicesDiscovered();
                        }
                    }
                });
            }

        });
    }

    public boolean read(String uuid_service, String uuid_read, BleCharacterCallback callback) {
        return bleManager.readDevice(uuid_service, uuid_read, callback);
    }

    public boolean write(String uuid_service, String uuid_write, String hex, BleCharacterCallback
            callback) {
        return bleManager.writeDevice(uuid_service, uuid_write, HexUtil.hexStringToBytes(hex),
                callback);
    }

    public boolean notify(String uuid_service, String uuid_notify, BleCharacterCallback callback) {
        return bleManager.notify(uuid_service, uuid_notify, callback);
    }

    public boolean indicate(String uuid_service, String uuid_indicate, BleCharacterCallback
            callback) {
        return bleManager.indicate(uuid_service, uuid_indicate, callback);
    }

    public boolean stopNotify(String uuid_service, String uuid_notify) {
        return bleManager.stopNotify(uuid_service, uuid_notify);
    }

    public boolean stopIndicate(String uuid_service, String uuid_indicate) {
        return bleManager.stopIndicate(uuid_service, uuid_indicate);
    }

    public boolean readRssi(BleRssiCallback callback) {
        return bleManager.readRssi(callback);
    }

    public void closeConnect() {
        bleManager.closeBluetoothGatt();
    }


    private void resetInfo() {
        name = null;
        mac = null;
        gatt = null;
        service = null;
        characteristic = null;
        charaProp = 0;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setService(BluetoothGattService service) {
        this.service = service;
    }

    public BluetoothGattService getService() {
        return service;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }

    public int getCharaProp() {
        return charaProp;
    }


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }

    public boolean isConnected(){
        return bleManager.isConnected();
    }

    public static BleManager getBleManager() {
        return bleManager;
    }

}
