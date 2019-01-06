package com.moscase.shouhuan.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.moscase.shouhuan.utils.MyApplication;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.moscase.shouhuan.utils.MyApplication.STATE_DISCONNECTED;
import static com.moscase.shouhuan.utils.MyApplication.hexStringToBytes;

public class MyBleService extends Service {
    private final static String TAG = MyBleService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private BluetoothDevice mBLEDevice;


    //特征列表集
    private List<BluetoothGattCharacteristic> mCharacteristics;

    private String mDeviceAddress = null;

    private SharedPreferences mSharedPreferences;

    private int num = 111;
    private TelephonyManager mTm;
    private Handler smsHandler;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private Uri SMS_INBOX = Uri.parse("content://sms");
    private MyPhoneListener mMyPhoneListener;

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();
    private SmsObserver mSmsObserver;

    public class LocalBinder extends Binder {
        public MyBleService getService() {
            return MyBleService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);


        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //创建一个监听器，监听电话呼叫状态的变化。
        mMyPhoneListener = new MyPhoneListener();
        //开始监听用户的通话状态
        mTm.listen(mMyPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        mSmsObserver = new SmsObserver(this, smsHandler);
        //注册一个内容观察者监听短信的变化
        getContentResolver().registerContentObserver(SMS_INBOX, true,
                mSmsObserver);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        //BluetoothGattCallback 里面有很多方法，但并非所有都需要在开发当中用到。
        //这里列出来只是作为部分解析，需要哪个方法，就重写哪个方法，不需要的，直接去掉。
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED && num == 111) {
                num = 222;
                mSharedPreferences.edit().putBoolean("isconnected", true).commit();
                MyApplication.isBleConnect = true;
                Intent intent = new Intent("lianjiechenggong");
                sendBroadcast(intent);

                gatt.getDevice();
                //自调用发现服务
                gatt.discoverServices();


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED && num == 222) {
                num = 111;
                Intent intent = new Intent("com.chenhang.disconnect");
                sendBroadcast(intent);
                mSharedPreferences.edit().putBoolean("isconnected", false).commit();
                MyApplication.isBleConnect = false;
                //先关闭连接释放资源再重新连接
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        connect(mDeviceAddress);
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 1000);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED && num == 111) {
                //先关闭连接释放资源再重新连接
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        connect(mDeviceAddress);
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 1000);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //当发现设备服务时，会回调到此处。

            if (status == BluetoothGatt.GATT_SUCCESS) {     //服务全部发现完成
                Intent intent = new Intent("discoverservices");
                sendBroadcast(intent);

            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //读取特征后回调到此处。

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //如果程序执行到这里，证明特征的读取已经完成，可以在回调当中取出特征的值。
                //特征所包含的值包含在一个byte数组内，可以定义一个临时变量来获取。

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Intent intent = new Intent("elc");
                    intent.putExtra("elc", characteristic.getValue());
                    sendBroadcast(intent);

                }


            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //写入特征后回调到此处。
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("koma", "写入成功");
            } else {
                Log.d("koma", "写入失败");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //当特征（值）发生变法时回调到此处。
            //当决定用通知的方式获取外设特征值的时候，每当特征值发生变化，程序就会回调到此处。
            //在一个gatt链接当中，可以同时存在多个notify的回调，全部值都会回调到这里，那么如何区分这些值的来源？
            //这个时候，就需要去判断回调回来的特征的UUID，因为UUID是唯一的，所以可以用UUID
            //来确定，这些数据来自哪个特征。
            //假设已经定了多个想要使用的静态UUID，已经说过如何表达一个UUID
            //那么需要做的就是对比这些UUID，根据不同的UUID来分类这些数据，究竟应该交由哪个方法来处理
            //所以，这么一来便会发现其实上面的onCharacteristicRead也会出现这种情况，
            //因为不可能只读取一个特征，除非这个外设也只有这一个特征，
            //究竟是谁在读取，读取的值来自于哪个特征等，都需要进行判断。

            byte[] value = characteristic.getValue();

            Intent intent = new Intent("callback");
            intent.putExtra("byte", value);
            sendBroadcast(intent);

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int
                status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //读取描述符后回调到此处。
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int
                status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //写入描述符后回调到此处
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            //暂时没有用过。
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //Rssi表示设备与中心的信号强度，发生变化时回调到此处。
            if (status == BluetoothGatt.GATT_SUCCESS) { //读取信号强度成功的时候
                Intent intent = new Intent("rssi");
                intent.putExtra("rssi", rssi);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            //暂时没有用过。
        }
    };


    //读取特征，相当简单，一句话带过，读取结果会回调到mBluetoothGattCallback中的onCharacteristicRead。
    public void read() {
        BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString
                ("0000180f-0000-1000-8000-00805f9b34fb"));
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID
                .fromString("00002a19-0000-1000-8000-00805f9b34fb"));
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    //写入特征，也相当简单，一句话带过，读取结果会回调到mBluetoothGattCallback中的onCharacteristicWrite
    public boolean write(byte[] bytes) {
        boolean b = false;
        if (mBluetoothGatt != null) {
            BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString
                    ("0000ffe5-0000-1000-8000-00805f9b34fb"));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID
                    .fromString("0000ffe9-0000-1000-8000-00805f9b34fb"));
            characteristic.setValue(bytes);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            b = mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return b;
    }

    //设置通知，读取结果会回调到mBluetoothGattCallback中的onCharacteristicChanged
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                               boolean enable) {
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        //下面的几句代码建议写上。
        //在明确知道当前特征的描述符前提下，可以直接使用描述符，不需要做判断，
        //但如果不知道此特征是否具有描述符的情况下，没有以下几行代码可能会导致设置通知失败的情况发生。
        List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
        if (descriptorList != null) {
            for (BluetoothGattDescriptor descriptor : descriptorList) {
                byte[] value = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(value);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }


    @Override
    public void onDestroy() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }


    public void connect(String deviceAddress) {
        mDeviceAddress = deviceAddress;
        if (mBluetoothAdapter == null || deviceAddress == null)
            return;
        mBluetoothGatt = MyApplication.sBluetoothDevice.connectGatt(this, false,
                mBluetoothGattCallback);

    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }


    private class MyPhoneListener extends PhoneStateListener {

        //当手机呼叫状态变化的时候 执行下面代码。
        //state 电话的状态
        //incomingnumber 来电号码
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                //判断我们当前手机的通话状态
                switch (state) {
                    //手机处于空闲状态，没有人打电话 没有零响
                    case TelephonyManager.CALL_STATE_IDLE:

                        break;
                    //手机零响状态
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d("koma","发送了电话");
                        write(hexStringToBytes("phone"));


                        break;
                    //电话接通状态，用户正在打电话
                    case TelephonyManager.CALL_STATE_OFFHOOK:


                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean flag = true;
    class SmsObserver extends ContentObserver {
        private Context mContext;
        String[] projection = new String[]{"address", "body", "date", "type", "read"};

        public SmsObserver(Context context, Handler handler) {
            super(handler);
            mContext = context;
        }


        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法


            if (flag) {
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor c = mContext.getContentResolver().query(uri, projection, null, null, "date" +
                        " desc");//"date desc"


                if (c != null) {

                    if (c.moveToNext()) {
                        //得到接收到短信的电话号码
                        String address = c.getString(c.getColumnIndex("address"));
                        //得到接收到短信的内容
                        String body = c.getString(c.getColumnIndex("body"));
                        Log.d("koma","发送了短信");
                        write(hexStringToBytes("message"));


                    }
                    c.close();
                    flag = false;
                }
            }


        }

    }


}
