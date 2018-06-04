package com.moscase.shouhuan.utils;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.inuker.bluetooth.library.BluetoothClient;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.exceptions.GlobalException;

/**
 * Created by 陈航 on 2017/8/3.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class MyApplication extends LitePalApplication {
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Global application context.
     */
    static Context sContext;

    /**
     * 当前MainActivity是否已经resume
     */
    public static boolean isResume;

    /**
     * 当前是否已经进入了拍照页面
     */
    public static boolean isEnterPhotoActivity;
    public static boolean isFirstChanglianjie = true;

    public final static String XmlPath = "https://app.moscase8.com/apps/538/BluetoothWatch.xml";
    public final static int LOGIN = 10000;

    /**
     * 是否是英制单位
     */
    public static boolean isInch = false;

    public static BluetoothClient sBluetoothClient;


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

    public static BluetoothClient getBleManager() {
        if (sBluetoothClient == null) {
            sBluetoothClient = new BluetoothClient(getContext());
        }
        return sBluetoothClient;
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


        //上传BUG日志的初始化
        CrashHandler.getInstance().init(this);
    }


    public static String toHexString1(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase?DIGITS_LOWER:DIGITS_UPPER);
    }

    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        if(data == null) {
            return null;
        } else {
            int l = data.length;
            char[] out = new char[l << 1];
            int i = 0;

            for(int var5 = 0; i < l; ++i) {
                out[var5++] = toDigits[(240 & data[i]) >>> 4];
                out[var5++] = toDigits[15 & data[i]];
            }

            return out;
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if(hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    public static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

}
