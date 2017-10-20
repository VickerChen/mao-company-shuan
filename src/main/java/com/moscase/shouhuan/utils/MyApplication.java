package com.moscase.shouhuan.utils;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.clj.fastble.BleManager;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.exceptions.GlobalException;

/**
 * Created by 陈航 on 2017/8/3.
 *
 * 少年一事能狂  敢骂天地不仁
 */

public class MyApplication extends LitePalApplication {

    /**
     * Global application context.
     */
    static Context sContext;

    public static BleManager sBleManager;

    /**
     * 当前MainActivity是否已经resume
     */
    public static boolean isResume;

    /**
     * 当前是否已经进入了拍照页面
     */
    public static boolean isEnterPhotoActivity;

    public final static String XmlPath = "https://app.moscase8.com/apps/538/BluetoothWatch.xml";




    /**
     * Construct of LitePalApplication. Initialize application context.
     */
    public MyApplication() {
        sContext = this;
    }

    /**
     * Deprecated. Use {@link LitePal#initialize(Context)} instead.
     * @param context
     * 		Application context.
     */
    @Deprecated
    public static void initialize(Context context) {
        sContext = context;
    }

    /**
     * Get the global application context.
     *
     * @return Application context.
     * @throws org.litepal.exceptions.GlobalException
     */
    public static Context getContext() {
        if (sContext == null) {
            throw new GlobalException(GlobalException.APPLICATION_CONTEXT_IS_NULL);
        }
        return sContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
//        SDKInitializer.initialize(this);
//        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置使用的坐标类型.
//        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
//        SDKInitializer.setCoordType(CoordType.BD09LL);

        /**
         *在Android N的系统上，Android N对访问文件权限收回，按照Android N的要求，若要在应用间共享文件，
         *应发送一项 content://URI，并授予 URI 临时访问权限。而进行此授权的最简单方式是使用 FileProvider类。
         *这里我没有用上述方式，如果不做这一步的配置的话在我的信息界面调用系统相机时会崩掉
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

    }

    public static synchronized BleManager getBleManager(){
        if (sBleManager == null)
            sBleManager = new BleManager(getContext());
        return sBleManager;
    }

    public static String toHexString1(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

}
