package com.moscase.shouhuan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elbbbird.android.socialsdk.SocialSDK;
import com.elbbbird.android.socialsdk.model.SocialToken;
import com.elbbbird.android.socialsdk.model.SocialUser;
import com.elbbbird.android.socialsdk.otto.BusProvider;
import com.elbbbird.android.socialsdk.otto.SSOBusEvent;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.BushuData;
import com.moscase.shouhuan.bean.MyInfoBean;
import com.moscase.shouhuan.bean.UpdataInfo;
import com.moscase.shouhuan.fragment.XinlvFragment;
import com.moscase.shouhuan.fragment.ZhibiaoFragment;
import com.moscase.shouhuan.fragment.ZhuangtaiFragment;
import com.moscase.shouhuan.service.MyBleService;
import com.moscase.shouhuan.utils.ComputeMyInfo;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.ParseXml;
import com.nineoldandroids.view.ViewHelper;
import com.tencent.connect.common.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Environment.getExternalStorageDirectory;
import static com.aurelhubert.ahbottomnavigation.AHBottomNavigation.TitleState.ALWAYS_SHOW;
import static com.moscase.shouhuan.utils.MyApplication.encodeHexStr;
import static com.moscase.shouhuan.utils.MyApplication.hexStringToBytes;
import static com.moscase.shouhuan.utils.MyApplication.isEnterPhotoActivity;
import static com.moscase.shouhuan.utils.MyApplication.isFirstChanglianjie;
import static com.moscase.shouhuan.utils.MyApplication.isResume;

/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class MainActivity extends AppCompatActivity {
    //两边的抽屉
    private DrawerLayout mDrawerLayout;
    //心率fragment
    private XinlvFragment mXinlvFragment;
    //指标fragment
    private ZhibiaoFragment mZhibiaoFragment;
    //状态fragment
    private ZhuangtaiFragment mZhuangtaiFragment;
    //地步的三个bar
    private AHBottomNavigation mBottomNavigation;
    //是否是第一次进入APP，我要把个人信息数据存到数据库，不然第一次打开info的时候会崩溃
    private boolean isFistEnterAPP = false;
    //更新界面的步数
    private String bushu;
    //需要传给手表的是否是24小时制
    private String mTimeFormat;
    //更新界面的总距离
    private float mGongli;
    //更新界面的千卡
    private float mKaluli;

    //    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;

    //从提醒界面拿到是否是短连接，用于发送给手表
    private boolean mIsDuanlianjie;

    private boolean mIsBindService;


    private SharedPreferences mDuanlianjie;
    private SharedPreferences mMyInfoShared;


    /**
     * 这个日期，是用来往数据库里面存的，然后判断当前的步数是否是同一天
     * 如果是，就更新数据而不是插入
     */
    private String riqi;
    private int lastBushu;

    //崩溃日志保存的地址
    private static final String PATH =
            Environment.getExternalStorageDirectory() + "/Crash/log/";
    //这里要判断当前屏幕是否解锁，如果没解锁直接从538发过来跳转到Photo界面的话，直接崩溃
    private boolean isScreenOn = true;

    private MyInfoBean mMyInfoBean1;
    private String mTime;

    private MyBleService mMyBleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = getSharedPreferences("isFirstEnterAPP", MODE_PRIVATE);
        isFistEnterAPP = sharedPreferences.getBoolean("isFistEnterAPP", false);
        mMyInfoShared = getSharedPreferences("myInfo", MODE_PRIVATE);
        //具体逻辑是，先从数据库拿到日期和步数，假如手表连接上的话，就判断是否是当天的数据，
        //如果不是再判断是否比上一次的步数大，因为要存最大的数据
        lastBushu = mMyInfoShared.getInt("lastbushu", 0);
        mDuanlianjie = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        //万一是因为崩溃导致的退出，进来的时候先设置为未连接，不然的话进入扫描界面还会显示当前连接s
        mDuanlianjie.edit().putBoolean("isconnected", false).commit();
        mIsDuanlianjie = mDuanlianjie.getBoolean("isChecked", true);
        //本来是用来存储长短连接的数据库，我拿来顺便存一个是否是英制
        MyApplication.isInch = mDuanlianjie.getBoolean("isinch", false);

        //每次进入APP的时候会根据条件来判断是否上传错误日志
        Log.d("koma", "isCrash:" + mDuanlianjie.getBoolean("isCrash", false));
        if (mDuanlianjie.getBoolean("isCrash", false)) {
            uploadFile();
            mDuanlianjie.edit().putBoolean("isCrash", false).commit();
        }


        //在第一次进入APP的时候，把所有的默认数据都设置好存到数据库里
        if (!isFistEnterAPP) {
            mMyInfoBean1 = new MyInfoBean();
            mMyInfoBean1.setSex("男");
            mMyInfoBean1.setBirthday(1989);
            mMyInfoBean1.setYaowei(70);
            mMyInfoBean1.setTunwei(100);
            mMyInfoBean1.setShengao(178);
            mMyInfoBean1.setTizhong(68);
            mMyInfoBean1.save();
            isFistEnterAPP = true;
            sharedPreferences.edit().putBoolean("isFistEnterAPP", isFistEnterAPP).commit();
        }

        //当APP和手表已经连接上之后断开的时候，会重新连接，重新连接成功后发送广播，提醒主页面notify
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.chenhang.reconnect");
        filter.addAction("com.chenhang.disconnect");
        filter.addAction("lianjiechenggong");
        filter.addAction("discoverservices");
        filter.addAction("callback");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);

        //注册事件
        BusProvider.getInstance().register(this);//QQ登录
        EventBus.getDefault().register(this);


        //来一只超级玛丽助助兴      --Create by chenhang
        System.out.print("             来一只超级玛丽助助兴！  \n" +
                "                ********\n" +
                "               ************\n" +
                "               ####....#.\n" +
                "             #..###.....##....\n" +
                "             ###.......######              ###            ###\n" +
                "                ...........               #...#          #...#\n" +
                "               ##*#######                 #.#.#          #.#.#\n" +
                "            ####*******######             #.#.#          #.#.#\n" +
                "           ...#***.****.*###....          #...#          #...#\n" +
                "           ....**********##.....           ###            ###\n" +
                "           ....****    *****....\n" +
                "             ####        ####\n" +
                "           ######        ######\n" +
                "##############################################################\n" +
                "#...#......#.##...#......#.##...#......#.##------------------#\n" +
                "###########################################------------------#\n" +
                "#..#....#....##..#....#....##..#....#....#####################\n" +
                "##########################################    #----------#\n" +
                "#.....#......##.....#......##.....#......#    #----------#\n" +
                "##########################################    #----------#\n" +
                "#.#..#....#..##.#..#....#..##.#..#....#..#    #----------#\n" +
                "##########################################    ############\n");

//        这个是ViewPager的监听事件，这里我做了两手准备，本来用的是fragment的来回切换，用add方法
//        后来想想又用了ViewPager的嵌套滑动切换fragment，想换回ViewPager的话那就在代码和布局里面相互注释和反注释掉就好了
//        initDatas();
        initView();
        initEvents();
        initBottomBar();


        setDefaultFragment();

        //检查版本更新
        check();


    }


    //底部的三个bar
    private void initBottomBar() {
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("状态", R.drawable.zhuangtai);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("心率", R.drawable.healthy);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("指标", R.drawable.zhibiao);


        mBottomNavigation.setTitleState(ALWAYS_SHOW);


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

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mZhuangtaiFragment);
        mFragmentList.add(mXinlvFragment);
        mFragmentList.add(mZhibiaoFragment);
    }

    //打开左侧抽屉
    public void OpenLeftMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    //隐藏其他fragment
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

    //设置进入APP后默认的fragment
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


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mMyBleService = ((MyBleService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public void notifyMainActivity() {
        Log.d("koma===", "开始注册通知");
        BluetoothGatt bluetoothGatt = mMyBleService.getBluetoothGatt();
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString
                ("0000ffe0-0000-1000-8000-00805f9b34fb"));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString
                ("0000ffe4-0000-1000-8000-00805f9b34fb"));

        boolean isEnableNotification = bluetoothGatt.setCharacteristicNotification
                (characteristic, true);

//        注：如果只写上面部分，那么是收不到通知的
        if (isEnableNotification) {
            Log.d("koma===", "注册通知成功");
            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
            if (descriptorList != null && descriptorList.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                }
            }
        } else {
            Log.d("koma===", "注册通知失败");
        }


    }

    //传到状态fragment去更新界面
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
            //注册notify的消息
            notifyMainActivity();
        } else if (event.getMsg() == 111) {
            //连接BLE的消息
        }

    }

    //getDate
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

        //从闹钟界面拿到存储的开或者关
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
        if (MyApplication.isInch) {
            //身高
            String shengao = mMyInfoShared.getInt("heightft", 5) + "";
            String temp = mMyInfoShared.getInt("heightinch", 11) + "";
            if (Integer.parseInt(temp) < 10)
                temp = "0" + temp;
            shengao = shengao + temp;

            //体重
            String tizhong = mMyInfoShared.getInt("weightft", 150) + "";

            String buchang = mMyInfoShared.getInt("buchangft", 30) + "";

            shengao = "0" + shengao;

            if (Integer.parseInt(tizhong) < 100) {
                tizhong = "00" + tizhong;
            } else {
                tizhong = "0" + tizhong;
            }
            buchang = "00" + buchang;

            //获取闹钟是否打开
            String temp1 = "";
            SharedPreferences sharedPreferences = getSharedPreferences("togglebuttonstatus",
                    Context.MODE_PRIVATE);

            String hour = sharedPreferences.getString("hour", "12");
            String min = sharedPreferences.getString("min", "00");
            temp1 += hour;
            temp1 += min;

            //从闹钟界面拿到存储的开或者关
            boolean alarm1 = sharedPreferences.getBoolean("alarm1", false);
            boolean alarm2 = sharedPreferences.getBoolean("alarm2", false);
            boolean snooze = sharedPreferences.getBoolean
                    ("isSnooze", false);
            //闹钟关闭
            if (!alarm1 && !alarm2 && !snooze) {
                temp1 += "00";
            } else if (alarm1 && !alarm2 && !snooze) {
                temp1 += "01";
            } else if (!alarm1 && alarm2 && !snooze) {
                temp1 += "02";
            } else if (alarm1 && !alarm2 && snooze) {
                temp1 += "03";
            } else if (!alarm1 && alarm2 && snooze) {
                temp1 += "04";
            }


            String data = shengao + buchang + tizhong + temp1 + "7f";
            Log.d("koma---身高", shengao);
            Log.d("koma---体重", tizhong);
            Log.d("koma---步长", buchang);
            Log.d("koma---总", data);
            return data;
        } else {
            String shengao = String.valueOf((int) DataSupport.find(MyInfoBean.class, 1)
                    .getShengao());
            String tizhong = String.valueOf((int) DataSupport.find(MyInfoBean.class, 1)
                    .getTizhong());
            //步长是后面加上来的，我懒得弄了，直接用share吧,前面是以前写的我直接放到数据库里面
            String buchang = String.valueOf(mMyInfoShared.getInt("buchang", 75));

            shengao = "0" + shengao;

            //获取闹钟是否打开
            String temp1 = "";
            SharedPreferences sharedPreferences = getSharedPreferences("togglebuttonstatus",
                    Context.MODE_PRIVATE);

            String hour = sharedPreferences.getString("hour", "12");
            String min = sharedPreferences.getString("min", "00");
            temp1 += hour;
            temp1 += min;

            //从闹钟界面拿到存储的开或者关
            boolean alarm1 = sharedPreferences.getBoolean("alarm1", false);
            boolean alarm2 = sharedPreferences.getBoolean("alarm2", false);
            boolean snooze = sharedPreferences.getBoolean
                    ("isSnooze", false);
            //闹钟关闭
            if (!alarm1 && !alarm2 && !snooze) {
                temp1 += "00";
            } else if (alarm1 && !alarm2 && !snooze) {
                temp1 += "01";
            } else if (!alarm1 && alarm2 && !snooze) {
                temp1 += "02";
            } else if (alarm1 && !alarm2 && snooze) {
                temp1 += "03";
            } else if (!alarm1 && alarm2 && snooze) {
                temp1 += "04";
            }

            if (DataSupport.find(MyInfoBean.class, 1).getTizhong() < 100) {
                tizhong = "00" + tizhong;
            } else {
                tizhong = "0" + tizhong;
            }
            buchang = "00" + buchang;
            String data = shengao + buchang + tizhong + temp1 + "7f";
            Log.d("koma---身高", shengao);
            Log.d("koma---体重", tizhong);
            Log.d("koma---步长", buchang);
            Log.d("koma---总", data);
            return data;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            SocialSDK.oauthQQCallback(requestCode, resultCode, data);
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals("com.chenhang.reconnect")) {
                Toast.makeText(MainActivity.this, "已连接！", Toast.LENGTH_SHORT).show();
                notifyMainActivity();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d("koma---", "锁屏");
                isScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.d("koma---", "解锁");
                isScreenOn = true;
            } else if (intent.getAction().equals("com.chenhang.disconnect")) {
                Toast.makeText(MainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals("lianjiechenggong")) {
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(MainActivity.this, MyBleService.class);
                if (!mIsBindService){
                    bindService(intent1, conn, BIND_AUTO_CREATE);
                    mIsBindService = true;
                }
                Log.d("koma==", "主界面绑定服务");
            } else if (intent.getAction().equals("discoverservices")) {
                notifyMainActivity();
            } else if (intent.getAction().equals("callback")) {
                byte[] value = intent.getByteArrayExtra("byte");
                String value1 = encodeHexStr(value);
                Log.d("koma---收到的数据", value1);
                //接收到数据之后，具体看协议上面，我这里直接判断低位的数字是几
                final String value2 = String.valueOf(value1.charAt(1));

                if (value1.length() == 38) {
                    if (value2.equals("1")) {
                        Log.d("koma---截取的是", "1");
                        bushu = value1.substring(16, 22);
                        mGongli = (Float.parseFloat(value1.substring(22, 28)) / 1.0f
                                / 100);
                        mKaluli = (Float.parseFloat(value1.substring(28, 34)) / 1.0f
                                / 10 / 1000);

                        if (value1.substring(2, 4).equals("01") && !MyApplication
                                .isInch) {
                            sendBroadcast(new Intent("com.chenhang.inch"));

                            MyApplication.isInch = true;
                            //本来是用来存储长短连接的数据库，我拿来顺便存一个是否是英制
                            mDuanlianjie.edit().putBoolean("isinch", MyApplication
                                    .isInch).commit();
                            mMyInfoShared.edit().clear().commit();
                        } else if (value1.substring(2, 4).equals("00") && MyApplication
                                .isInch) {
                            sendBroadcast(new Intent("com.chenhang.inch"));
                            //切换中英制
                            ComputeMyInfo.getInstance().setSex("男");
                            ComputeMyInfo.getInstance().setBirthday(1989);
                            ComputeMyInfo.getInstance().setYaowei(70);
                            ComputeMyInfo.getInstance().setTunwei(100);
                            ComputeMyInfo.getInstance().setShengao(178);
                            ComputeMyInfo.getInstance().setTizhong(68);
                            //本来是用来存储长短连接的数据库，我拿来顺便存一个是否是英制
                            MyApplication.isInch = false;
                            mDuanlianjie.edit().putBoolean("isinch", MyApplication
                                    .isInch).commit();
                        }

                        if (riqi.equals(get())) {
                            ContentValues values = new ContentValues();
                            if (lastBushu < Integer.parseInt(bushu)) {
                                lastBushu = Integer.parseInt(bushu);
                                mMyInfoShared.edit().putInt("lastbushu", lastBushu)
                                        .commit();
                                values.put("bushu", lastBushu);
                            }
                            values.put("sporttime", mTime);
                            values.put("kaluli", mKaluli * 1000);
                            values.put("distance", mGongli);
                            DataSupport.updateAll(BushuData.class, values, "riqi = ?", get());

                        } else {
                            BushuData bushuData = new BushuData();
                            bushuData.setRiqi(get());
                            bushuData.setBushu(Integer.parseInt(bushu));
                            bushuData.setDistance((int) mGongli);
                            bushuData.setKaluli(mKaluli * 1000);
                            bushuData.setSporttime(mTime);
                            bushuData.save();

                            riqi = get();
                            lastBushu = Integer.parseInt(bushu);
                            mMyInfoShared.edit().putString("riqi", get()).commit();
                        }

                    } else {
                        Log.d("koma 截取的是", "2");
                        mTime = value1.substring(8, 14);
                        int step = Integer.parseInt(bushu);
                        Intent intent1 = new Intent();


                        intent1.setAction("com.chenhang.time");
                        intent1.putExtra("time", mTime);
                        intent1.putExtra("step", step);
                        sendBroadcast(intent1);
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                byte[] value = intent.getByteArrayExtra("byte");

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

                                    mMyBleService.write(hexStringToBytes(getDate()));

                                } else if (MyApplication.toHexString1
                                        (value[0]).equals("6f") &&
                                        MyApplication.toHexString1
                                                (value[1]).
                                                equals("6b") &&
                                        MyApplication.toHexString1
                                                (value[2]).equals
                                                ("7f")) {
                                    //如果收到OK
                                    Log.d("koma", "接收到OK消息");
                                } else if (MyApplication.toHexString1(value[0])
                                        .equals("70") &&
                                        MyApplication.toHexString1(value[1])
                                                .equals("68") && MyApplication
                                        .toHexString1(value[2]).equals("6f") &&
                                        MyApplication.toHexString1(value[3])
                                                .equals("74") && MyApplication
                                        .toHexString1(value[4]).equals("6f")) {
                                    if (isScreenOn) {
                                        //收到photo，发OK然后跳转界面
                                        mMyBleService.write(hexStringToBytes("6f6b"));

                                        if (!isEnterPhotoActivity) {

                                            isEnterPhotoActivity = true;
                                            Intent intent = new Intent(MainActivity
                                                    .this, PhotoActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                } else if (MyApplication.toHexString1(value[0])
                                        .equals("73") &&
                                        MyApplication.toHexString1(value[1])
                                                .equals("6e") && MyApplication
                                        .toHexString1(value[2]).equals("61") &&
                                        MyApplication.toHexString1(value[3])
                                                .equals("70")) {
                                    //收到snap，

                                    EventBus.getDefault().post(new MessageEvent
                                            (73));
                                } else if (MyApplication.toHexString1(value[0])
                                        .equals("22")) {
                                    Log.d("koma", "第二个包");
                                    //前两位是22代表是第二个包，此时需要拿到步数更新界面
                                    updateUI();
                                    final String temp;
                                    //这里，每次发数据前先检查是长连接还是短连接
                                    mIsDuanlianjie = mDuanlianjie.getBoolean
                                            ("isChecked", true);
                                    if (!mIsDuanlianjie) {
                                        temp = "6f6e" + getInfo();
                                        Log.d("koma----", temp);
                                    } else {
                                        temp = "6f6666" + getInfo();
                                        Log.d("koma----", temp);
                                    }

                                    boolean write = mMyBleService.write(hexStringToBytes(temp));
                                    if (write) {
                                        if (mDuanlianjie.getBoolean
                                                ("isChecked", true)) {
                                            if (isFirstChanglianjie)
                                                Toast.makeText
                                                        (MainActivity.this,
                                                                "对时成功", Toast.LENGTH_SHORT)
                                                        .show();
                                            Toast.makeText(MainActivity
                                                    .this, "短连接", Toast
                                                    .LENGTH_SHORT).show();
                                        } else {
                                            if (isFirstChanglianjie) {
                                                Toast.makeText
                                                        (MainActivity.this,
                                                                "对时成功", Toast.LENGTH_SHORT)
                                                        .show();
                                                Toast.makeText(MainActivity
                                                        .this, "长连接", Toast
                                                        .LENGTH_SHORT)
                                                        .show();
                                                isFirstChanglianjie = false;
                                            }

                                        }
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
        riqi = mMyInfoShared.getString("riqi", "2017-10-23");
        System.gc();

        super.onResume();
    }

    @Override
    protected void onPause() {
        isResume = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        unregisterReceiver(myReceiver);
        if (mIsBindService)
            unbindService(conn);
        BusProvider.getInstance().unregister(this);
        SocialSDK.revokeQQ(this);
        mDuanlianjie.edit().putBoolean("isconnected", false).commit();
        super.onDestroy();
    }


    /**
     * 下面这些代码是QQ登录的回调方法
     * 本来是准备调用LoginQQActivity代码复用一下的
     * 发现有点问题，我就单独写了
     */
    @Subscribe
    public void onOauthResult(SSOBusEvent event) {
        switch (event.getType()) {
            case SSOBusEvent.TYPE_GET_TOKEN:
                SocialToken token = event.getToken();
                Log.i("koma---QQ", "onOauthResult#BusEvent.TYPE_GET_TOKEN " + token.toString());
                break;
            case SSOBusEvent.TYPE_GET_USER:

                SocialUser user = event.getUser();
                mMyInfoShared.edit().putString("userName", user.getName()).commit();
                if (user.getGender() == 1) {
                    mMyInfoShared.edit().putBoolean("isMale", true).commit();

                } else if (user.getGender() == 2) {
                    mMyInfoShared.edit().putBoolean("isMale", false).commit();
                } else {
                    mMyInfoShared.edit().putBoolean("isMale", true).commit();
                }

                new MyTask(user.getAvatar()).execute();


                break;
            case SSOBusEvent.TYPE_FAILURE:
                Exception e = event.getException();
                Log.i("koma---QQ", "授权失败 " + e.toString());
                break;
            case SSOBusEvent.TYPE_CANCEL:
                Log.i("koma---QQ", "用户取消授权");
                break;
        }
    }


    //从连接中获取bitmap
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //把bitmap保存到文件夹
    public void saveMyBitmap(String path, Bitmap mBitmap) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("koma---保存图片出错", e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;
        String murl;

        public MyTask(String url) {
            murl = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("koma", "开始下载图片");
            bitmap = returnBitMap(murl);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("koma", "开始保存图片");
            saveMyBitmap(getExternalStorageDirectory() +
                    "/蓝牙手表图片/UserPhoto.jpg", bitmap);

        }
    }


    /**
     * APP更新
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
                File sdcard = getExternalStorageDirectory();
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
                File sdcard = getExternalStorageDirectory();

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


    //这是要存到数据库中的日期
    String get() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        String date1 = dateFormat.format(date);
        return date1;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mDuanlianjie.edit().putBoolean("isconnected", false);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void uploadFile() {
        final File f = new File(PATH);
        // 判断路径是否存在
        if (!f.exists()) {
            return;
        }

        final List<File> files = Arrays.asList(f.listFiles());
        if (files.size() == 0) {
            return;
        }
        OkGo.<String>post("http://120.25.207.192:8080/apps/android/BLEWatch/538/servlet" +
                "/UploadHandleServlet")
                .tag(this)
                .isSpliceUrl(true)
                .addFileParams("file", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("koma", "body:" + response.body());
                        Log.d("koma", "上传成功");
                        // 删除成功后，删除本地 crash 文件
                        for (File file : files) {
                            boolean result = file.delete();
                            Log.d("koma", "文件删除");
                        }
//                        JSONObject jsonObject;
//                        try {
//                            jsonObject = new JSONObject(response.body());
//                            int retCode = jsonObject.optInt("ret");
//                            if (retCode == 0) {
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Log.d("koma", "上传失败" + response.body());
                    }
                });
    }
}
