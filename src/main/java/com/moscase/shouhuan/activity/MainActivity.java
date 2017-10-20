package com.moscase.shouhuan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.MyInfoBean;
import com.moscase.shouhuan.fragment.XinlvFragment;
import com.moscase.shouhuan.fragment.ZhibiaoFragment;
import com.moscase.shouhuan.fragment.ZhuangtaiFragment;
import com.moscase.shouhuan.service.BluetoothService;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.ParseXml;
import com.moscase.shouhuan.utils.UpdataInfo;
import com.nineoldandroids.view.ViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;
import static com.moscase.shouhuan.utils.MyApplication.isEnterPhotoActivity;
import static com.moscase.shouhuan.utils.MyApplication.isResume;

/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
public class MainActivity extends FragmentActivity {
    private DrawerLayout mDrawerLayout;
    private XinlvFragment mXinlvFragment;
    private ZhibiaoFragment mZhibiaoFragment;
    private ZhuangtaiFragment mZhuangtaiFragment;
    private AHBottomNavigation mBottomNavigation;
    private boolean isFistEnterAPP = false;
    private String bushu;
    private String mTimeFormat;
    private float mGongli;
    private float mKaluli;
    private String[] permissionSyorage = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    //    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    private BluetoothService mBluetoothService;
    private boolean mIsDuanlianjie;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 111) {

            }
        }
    };
    private SharedPreferences mDuanlianjie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //在第一次进入APP的时候就把默认信息给设定好存到数据库中
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstEnterAPP", MODE_PRIVATE);
        isFistEnterAPP = sharedPreferences.getBoolean("isFistEnterAPP", false);

        mDuanlianjie = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        mIsDuanlianjie = mDuanlianjie.getBoolean("isChecked", true);
        Log.d("当前连接", mIsDuanlianjie + "");
        if (!isFistEnterAPP) {
            MyInfoBean myInfoBean1 = new MyInfoBean();
            myInfoBean1.setSex("男");
            myInfoBean1.setBirthday(1989);
            myInfoBean1.setYaowei(70);
            myInfoBean1.setTunwei(100);
            myInfoBean1.setShengao(178);
            myInfoBean1.setTizhong(120);
            myInfoBean1.save();
            isFistEnterAPP = true;
            sharedPreferences.edit().putBoolean("isFistEnterAPP", isFistEnterAPP).commit();
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.chenhang.reconnect");
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);


        //注册事件
        EventBus.getDefault().register(this);
        bindService();

        initView();
        initEvents();
        initBottomBar();

//        这个是ViewPager的监听事件，这里我做了两手准备，本来用的是fragment的来回切换，用add方法
//        后来想想又用了ViewPager的嵌套滑动切换fragment，想换回ViewPager的话那就在代码和布局里面相互注释和反注释掉就好了
//        initDatas();

        setDefaultFragment();

        check();

    }

//    private void initDatas() {
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int
//                    positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position == 0) {
//                    mBottomNavigation.setCurrentItem(0);
//                } else if (position == 1) {
//                    mBottomNavigation.setCurrentItem(1);
//                } else if (position == 2) {
//                    mBottomNavigation.setCurrentItem(2);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }


    //底部的三个bar
    private void initBottomBar() {
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("状态", R.drawable.zhuangtai);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("心率", R.drawable.healthy);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("指标", R.drawable.zhibiao);

        // Add items
        mBottomNavigation.addItem(item1);
        mBottomNavigation.addItem(item2);
        mBottomNavigation.addItem(item3);
        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    if (mBottomNavigation.getCurrentItem() == 0) {
                        return true;
                    } else {
                        setDefaultFragment();
//                        mViewPager.setCurrentItem(0);
                    }
                } else if (position == 1) {
                    if (mBottomNavigation.getCurrentItem() == 1) {
                        return true;
                    } else {
                        initXinlvFragment();
//                        mViewPager.setCurrentItem(1);
                    }
                } else if (position == 2) {
                    if (mBottomNavigation.getCurrentItem() == 2) {
                        return true;
                    } else {
                        initZhibiaoFragment();
//                        mViewPager.setCurrentItem(2);
                    }
                }
                return true;
            }
        });
    }

    //打开右侧抽屉
    public void OpenRightMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.RIGHT);
    }

    //初始化抽屉事件
    private void initEvents() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {
                    float leftScale = 1 - 0.3f * scale;
                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 1);
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    mContent.invalidate();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                此行代码表示锁定右侧的抽屉，只能通过点击右上角才能滑出抽屉，不能通过侧滑
//                mDrawerLayout.setDrawerLockMode(
//                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void initView() {
        mBottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
//        此行代码表示锁定右侧的抽屉，只能通过点击右上角才能滑出抽屉，不能通过侧滑
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
//        此行代码使抽屉拉出后屏幕不变暗
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(mZhuangtaiFragment);
        mFragmentList.add(mXinlvFragment);
        mFragmentList.add(mZhibiaoFragment);
//        mViewPager.setAdapter(mFragmentPagerAdapter);
//        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    //打开左侧抽屉
    public void OpenLeftMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (mZhuangtaiFragment != null) {
            transaction.hide(mZhuangtaiFragment);
        }
        if (mXinlvFragment != null) {
            transaction.hide(mXinlvFragment);
        }
        if (mZhibiaoFragment != null) {
            transaction.hide(mZhibiaoFragment);
        }
    }

    private void setDefaultFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mZhuangtaiFragment == null) {
            mZhuangtaiFragment = new ZhuangtaiFragment();
            transaction.add(R.id.fl_content, mZhuangtaiFragment);
        }
        hideFragment(transaction);
        transaction.show(mZhuangtaiFragment);
        transaction.commit();
    }

    private void initXinlvFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mXinlvFragment == null) {
            mXinlvFragment = new XinlvFragment(this);
            transaction.add(R.id.fl_content, mXinlvFragment);
        }
        hideFragment(transaction);
        transaction.show(mXinlvFragment);
        transaction.commit();
    }

    private void initZhibiaoFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mZhibiaoFragment == null) {
            mZhibiaoFragment = new ZhibiaoFragment(this);
            transaction.add(R.id.fl_content, mZhibiaoFragment);
        }
        hideFragment(transaction);
        transaction.show(mZhibiaoFragment);
        transaction.commit();
    }

    FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter
            (getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    };

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
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

        }

        @Override
        public void onScanning(final ScanResult result) {

        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnectFail() {

        }

        @Override
        public void onDisConnected(BleException exp) {
            Toast.makeText(mBluetoothService, "断开连接", Toast.LENGTH_SHORT).show();

            Log.e("koma", "断开连接" + exp);
        }

        @Override
        public void onServicesDiscovered() {

        }

        @Override
        public void onConnectSuccess() {

        }
    };

    public boolean notifyMainActivity() {
        boolean notify = MyApplication.getBleManager().notify
                ("0000ffe0-0000-1000-8000-00805f9b34fb",
                        "0000ffe4-0000-1000-8000-00805f9b34fb"
                        , new BleCharacterCallback() {
                            @Override
                            public void onSuccess(final BluetoothGattCharacteristic
                                                          characteristic) {
                                byte[] value = characteristic
                                        .getValue();
                                String value1 = HexUtil.encodeHexStr(value);
                                Log.d("koma1+mainactivity", value1);
                                //接收到数据之后，具体看协议上面，我这里直接判断低位的数字是几
                                final String value2 = String.valueOf(value1.charAt(1));

                                if (value2.equals("1")) {
                                    Log.d("koma 截取的是", "1");

                                    mGongli = (Float.parseFloat(value1.substring(22, 28)) / 1.0f
                                            / 100);
                                    mKaluli = (Float.parseFloat(value1.substring(28, 34)) / 1.0f
                                            / 10 / 1000);
                                    Log.d("koma---公里", mGongli + "");
                                    Log.d("koma---卡路里", mKaluli + "");

                                } else {
                                    Log.d("koma 截取的是", "2");
                                    bushu = value1.substring(2, 8);
                                    Log.d("koma 步数是", bushu);
                                }


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                byte[] value = characteristic
                                                        .getValue();
                                                if (MyApplication.toHexString1
                                                        (value[0]).equals
                                                        ("74") &&
                                                        MyApplication.toHexString1
                                                                (value[1]).equals
                                                                ("69") &&
                                                        MyApplication.toHexString1
                                                                (value[2]).equals
                                                                ("6d") &&
                                                        MyApplication.toHexString1
                                                                (value[3]).equals
                                                                ("65") &&
                                                        MyApplication.toHexString1
                                                                (value[4]).equals
                                                                ("3f") &&
                                                        MyApplication.toHexString1
                                                                (value[5]).equals("7f")
                                                        ) {
                                                    //如果收到time？
                                                    boolean isSuccess = MyApplication
                                                            .getBleManager()
                                                            .writeDevice
                                                                    ("0000ffe5-0000-1000-8000-00805f9b34fb",
                                                                            "0000ffe9-0000-1000-8000-00805f9b34fb",
                                                                            HexUtil.hexStringToBytes
                                                                                    (getDate()),
                                                                            new BleCharacterCallback() {

                                                                                @Override
                                                                                public void
                                                                                onSuccess
                                                                                        (BluetoothGattCharacteristic
                                                                                                 characteristic) {

                                                                                }

                                                                                @Override
                                                                                public void
                                                                                onFailure
                                                                                        (BleException
                                                                                                 exception) {

                                                                                }

                                                                                @Override
                                                                                public void
                                                                                onInitiatedResult
                                                                                        (boolean result) {

                                                                                }
                                                                            });

                                                    if (isSuccess) {
                                                        Log.d("koma1", "写入time成功");
                                                    } else {
                                                        Log.d("koma1", "写入time失败");
                                                    }
                                                } else if (MyApplication.toHexString1
                                                        (value[0]).equals("6f") &&
                                                        MyApplication.toHexString1
                                                                (value[1]).
                                                                equals("6b") &&
                                                        MyApplication.toHexString1
                                                                (value[2]).equals
                                                                ("7f")) {
                                                    //如果收到OK
                                                    Log.d("koma1", "接收到OK消息");
                                                } else if (MyApplication.toHexString1(value[0])
                                                        .equals("70") &&
                                                        MyApplication.toHexString1(value[1])
                                                                .equals("68") && MyApplication
                                                        .toHexString1(value[2]).equals("6f") &&
                                                        MyApplication.toHexString1(value[3])
                                                                .equals("74") && MyApplication
                                                        .toHexString1(value[4]).equals("6f")) {
                                                    //收到photo，跳转界面
                                                    MyApplication.getBleManager().writeDevice
                                                            ("0000ffe5-0000-1000-8000-00805f9b34fb", "0000ffe9-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes
                                                                    ("6f6b"), new
                                                                    BleCharacterCallback() {
                                                                        @Override
                                                                        public void onSuccess
                                                                                (BluetoothGattCharacteristic
                                                                                         characteristic) {

                                                                        }

                                                                        @Override
                                                                        public void onFailure
                                                                                (BleException
                                                                                         exception) {

                                                                        }

                                                                        @Override
                                                                        public void
                                                                        onInitiatedResult
                                                                                (boolean result) {

                                                                        }
                                                                    });

                                                    if (!isEnterPhotoActivity) {
                                                        isEnterPhotoActivity = true;
                                                        Intent intent = new Intent(MainActivity
                                                                .this, PhotoActivity.class);
                                                        startActivity(intent);
                                                    }
                                                } else if (MyApplication.toHexString1(value[0])
                                                        .equals("73") &&
                                                        MyApplication.toHexString1(value[1])
                                                                .equals("6e") && MyApplication
                                                        .toHexString1(value[2]).equals("61") &&
                                                        MyApplication.toHexString1(value[3])
                                                                .equals("70")) {
                                                    //收到snap，拍照

                                                    EventBus.getDefault().post(new MessageEvent
                                                            (73));
                                                } else if (MyApplication.toHexString1(value[0])
                                                        .equals("22")) {
                                                    Log.d("koma", "第二个包");
                                                    //前两位是22代表是第二个包，此时需要拿到步数更新界面
                                                    updateUI();
                                                    String temp;
                                                    //这里，每次发数据前先检查是长连接还是短连接
                                                    mIsDuanlianjie = mDuanlianjie.getBoolean
                                                            ("isChecked", true);
                                                    if (!mIsDuanlianjie) {
                                                        temp = "6f6e" + getInfo();
                                                    } else {
                                                        temp = "6f6666" + getInfo();
                                                    }
                                                    boolean isSuccess = MyApplication
                                                            .getBleManager()
                                                            .writeDevice
                                                                    ("0000ffe5-0000-1000-8000-00805f9b34fb",
                                                                            "0000ffe9-0000-1000-8000-00805f9b34fb",
                                                                            //6f6666是短连接
                                                                            //6f6e是长连接
                                                                            HexUtil.hexStringToBytes
                                                                                    (temp),
                                                                            new BleCharacterCallback() {

                                                                                @Override
                                                                                public void
                                                                                onSuccess
                                                                                        (BluetoothGattCharacteristic
                                                                                                 characteristic) {

                                                                                }

                                                                                @Override
                                                                                public void
                                                                                onFailure
                                                                                        (BleException
                                                                                                 exception) {

                                                                                }

                                                                                @Override
                                                                                public void
                                                                                onInitiatedResult
                                                                                        (boolean result) {

                                                                                }
                                                                            });

                                                    if (isSuccess) {
                                                        Log.d("koma1", "写入长/短连接成功");
                                                        Log.d("koma---", temp);
                                                    } else {
                                                        Log.d("koma1", "写入长/短连接失败");
                                                    }
                                                } else if (MyApplication.toHexString1(value[0])
                                                        .equals("21")) {
                                                    //前两位是21代表是第一个包，此时拿到身高等信息
                                                    Log.d("koma", "第一个包");
                                                }
                                            }
                                        }, 1000);
                                    }
                                });


                            }

                            @Override
                            public void onFailure(BleException exception) {
                                Log.d("koma1", "特征值改变失败");
                            }

                            @Override
                            public void onInitiatedResult(boolean result) {

                            }
                        });
        if (notify) {
            Log.d("koma1", "主界面注册成功");
        } else {
            Log.d("koma1", "主界面注册失败");
        }
        return notify;
    }

    private void updateUI() {
        int result = Integer.parseInt(bushu);
        Log.d("bushushi+int", result + "");
        Log.d("bushushi+str", bushu);
        if (mZhuangtaiFragment == null) {
            mZhuangtaiFragment = new ZhuangtaiFragment();
            mZhuangtaiFragment.setAngel(result, mGongli, mKaluli);
        } else {
            mZhuangtaiFragment.setAngel(result, mGongli, mKaluli);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getMsg() == 123) {
            notifyMainActivity();
        }
    }

    private String getDate() {
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String date1 = dateFormat.format(date);
        //获得系统的时间制式
        ContentResolver cv = this.getContentResolver();
        mTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (("24").equals(mTimeFormat)) {
            //设置24小时制式
            date1 += "01";
//            0730007f
        } else {
            date1 += "00";
//            0730007f
        }

        //获取闹钟是否打开
        SharedPreferences sharedPreferences = getSharedPreferences("togglebuttonstatus",
                Context.MODE_PRIVATE);

        String hour = sharedPreferences.getString("hour", "12");
        String min = sharedPreferences.getString("min", "00");
        date1 += hour;
        date1 += min;

        boolean alarm1 = sharedPreferences.getBoolean("alarm1", false);
        boolean alarm2 = sharedPreferences.getBoolean("alarm2", false);
        boolean snooze = sharedPreferences.getBoolean
                ("isSnooze", false);
        //闹钟关闭
        if (!alarm1 && !alarm2 && !snooze) {
            date1 += "007f";
        } else if (alarm1 && !alarm2 && !snooze) {
            date1 += "017f";
        } else if (!alarm1 && alarm2 && !snooze) {
            date1 += "027f";
        } else if (alarm1 && !alarm2 && snooze) {
            date1 += "037f";
        } else if (!alarm1 && alarm2 && snooze) {
            date1 += "047f";
        }
//        date1 += "010730007f";
        Log.d("日期是", date1);
        return date1;
    }

    private String getInfo() {
//        "6f6e0170007500607f"
        SharedPreferences sharedPreferences = getSharedPreferences("myInfo", MODE_PRIVATE);
        String shengao = String.valueOf(DataSupport.find(MyInfoBean.class, 1).getShengao());
        String tizhong = String.valueOf(DataSupport.find(MyInfoBean.class, 1).getTizhong());
        String buchang = String.valueOf(sharedPreferences.getInt("buchang", 75));

        shengao = "0" + shengao;

        if (DataSupport.find(MyInfoBean.class, 1).getTizhong() < 100) {
            tizhong = "00" + tizhong;
        } else {
            tizhong = "0" + tizhong;
        }
        buchang = "00" + buchang;
        String data = shengao + buchang + tizhong + "7f";
        Log.d("koma---身高", shengao);
        Log.d("koma---体重", tizhong);
        Log.d("koma---步长", buchang);
        Log.d("koma---总", data);
        return data;
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            notifyMainActivity();
        }

    };


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
        isResume = true;
//        if (MyApplication.getBleManager().isInScanning()){
//            MyApplication.getBleManager().cancelScan();
//            scanOverScan();
//        }
//        scanOverScan();
        super.onResume();
    }

    @Override
    protected void onPause() {
        isResume = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        if (isEnabled()) {
//            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//        } else {
//            Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
//            toast.show();
//        }

        //取消注册事件
        EventBus.getDefault().unregister(this);
        unbindService(mFhrSCon);
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }


    /**
     * 自动更新
     */

    private ProgressDialog progressDialog;
    private int maxSize;
    // 设备的版本号
    private int versionCode;
    // 解析xml文件获得的对象
    private UpdataInfo ui;
    private boolean isUpdata = false;
    private UpdataInfo ui2;
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.arg1 == 1) {
                ui2 = (UpdataInfo) msg.obj;
                if (isUpdata) {
                    // 创建更新提示
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this); // 先得到构造器
                    builder.setTitle(getString(R.string.alert_title_new_version)); // 设置标题
                    builder.setMessage(getString(R.string.alert_message_wether_update)); // 设置内容
                    builder.setIcon(R.drawable.icon_app);// 设置图标，图片id即可
                    builder.setPositiveButton(getString(R.string.alert_button_update_now),
                            new DialogInterface.OnClickListener() { // 设置确定按钮
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    File path = Environment.getDataDirectory();
                                    StatFs stat = new StatFs(path.getPath());
                                    long blockSize = stat.getBlockSize();
                                    long availableBlocks = stat.getAvailableBlocks();
                                    if (availableBlocks * blockSize < 60000000) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                MainActivity.this); // 先得到构造器
                                        builder.setMessage("Unable to download the apk, please " +
                                                "retry after delete some items"); // 设置内容
                                        builder.setPositiveButton("Management application", new
                                                DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface
                                                                                dialogInterface,
                                                                        int i) {
                                                        Intent intent = new Intent();
                                                        intent.setAction("android.intent.action" +
                                                                ".MAIN");
                                                        intent.setClassName("com.android" +
                                                                ".settings", "com" +
                                                                ".android.settings" +
                                                                ".ManageApplications");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                        builder.setNegativeButton("Cancle", new DialogInterface
                                                .OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialogInterface,
                                                                int i) {
                                                finish();
                                            }
                                        }).create().show();
                                    } else {
                                        dialog.dismiss();
                                        if (Build.VERSION.SDK_INT >= M) {
                                            //判断当前系统的SDK版本是否大于23
                                            //如果当前申请的权限没有授权
                                            if (!(checkSelfPermission(Manifest.permission
                                                    .WRITE_EXTERNAL_STORAGE) == PackageManager
                                                    .PERMISSION_GRANTED)) {
                                                //第一次请求权限的时候返回false,
                                                // 第二次shouldShowRequestPermissionRationale返回true
                                                //如果用户选择了“不再提醒”永远返回false。
                                                if (shouldShowRequestPermissionRationale(android
                                                        .Manifest.permission.RECORD_AUDIO)) {
                                                    Toast.makeText(MainActivity.this, "请授予权限", Toast
                                                            .LENGTH_LONG).show();
                                                }
                                                //请求权限
                                                requestPermissions(new String[]{Manifest
                                                        .permission.WRITE_EXTERNAL_STORAGE}, 1);
                                            } else {//已经授权了就走这条分支
                                                Toast.makeText(MainActivity.this, getString(R.string
                                                                .alert_button_being_update),
                                                        Toast.LENGTH_SHORT).show();
                                                initDialog();

                                                MyTast mt = new MyTast(ui2.getUrl());
                                                mt.execute();
                                            }
                                        } else {//已经授权了就走这条分支

                                            Toast.makeText(MainActivity.this, getString(R.string
                                                            .alert_button_being_update),
                                                    Toast.LENGTH_SHORT).show();
                                            initDialog();

                                            MyTast mt = new MyTast(ui2.getUrl());
                                            mt.execute();
                                        }
                                    }
                                }
                            });
                    // 不更新则退出
                    builder.setNegativeButton("Exit",
                            new DialogInterface.OnClickListener() { // 设置取消按钮
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    builder.setCancelable(false);
                    // 参数都设置完成了，创建并显示出来
                    builder.create().show();
                }
            } else if (msg.arg1 == 0) {
//                Intent intent = new Intent();
//                intent.setClass(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        }
    };


    private void check() {
        // 获取设备版本号
        try {
            versionCode = this.getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionCode;
            Log.d("koma--版本号是", versionCode + "");
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }


        Thread thread = new Thread() {
            @Override
            public void run() {
                // 检查设备版本
                checkVersion();
                if (!isUpdata) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.arg1 = 0;
                    handler.sendMessage(msg);
                }
                super.run();
            }
        };
        thread.start();
    }

    public void checkVersion() {

        // 先从网络获取服务器上apk的版本号
        URL url = null;
        try {
            url = new URL(MyApplication.XmlPath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);    //设置三秒超时
            conn.setReadTimeout(2000);
            conn.connect();
            // 获取流
            InputStream is = conn.getInputStream();
//
            ui = new ParseXml().parse(is);

            if (Integer.parseInt(ui.getVersion()) > versionCode) {
                isUpdata = true;
            } else {
                isUpdata = false;
            }
            // 使用 message传递消息给主线程
            Message message = new Message();
            message.arg1 = 1;
            message.obj = ui;
            handler.sendMessage(message);

        } catch (MalformedURLException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }

    //异步下载更新的文件
    public class MyTast extends AsyncTask {
        String path;

        public MyTast(String path) {
            this.path = path;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            return downApk(path);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            File file1 = null;
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File sdcard = Environment.getExternalStorageDirectory();
                file1 = new File(sdcard, "update.apk");
            }
            //下载完成之后自动安装
            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file1),
//                    "application/vnd.android.package-archive");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.moscase" +
                        ".shouhuan.fileprovider", file1);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file1), "application/vnd.android" +
                        ".package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
            progressDialog.dismiss();
            finish();

        }
    }

    //下载文件
    public File downApk(String path) {
        File file = null;
        try {
            URL url = new URL(path);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            maxSize = conn.getContentLength();
            progressDialog.setMax(maxSize);
            // 判定SD卡是否能用 能用则下载文件
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File sdcard = Environment.getExternalStorageDirectory();

                file = new File(sdcard, "update.apk");
                // 获得输出流
                FileOutputStream fos = new FileOutputStream(file);

                // 将文件读取至本地
                int len = 0;
                byte[] buffer = new byte[512];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    progressDialog.setProgress(progressDialog.getProgress() + len);

                }
                // 清除缓存
                fos.flush();
                fos.close();
                is.close();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;

    }


    public void initDialog() {
        progressDialog = new ProgressDialog(this);
//        progressDialog.setIcon(R.drawable.icon_app);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMessage("Please wait a minute!");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条对话框//样式（水平，旋转）
        //显示
        progressDialog.setCancelable(false);
        progressDialog.show();
        //必须设置到show之后
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[]
                                                   grantResults) {
        if (requestCode == 1) {
            Toast.makeText(MainActivity.this, getString(R.string
                            .alert_button_being_update),
                    Toast.LENGTH_SHORT).show();
            initDialog();
            MyTast mt = new MyTast(ui2.getUrl());
            mt.execute();
        }

    }


}
