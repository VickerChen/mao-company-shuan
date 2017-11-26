package com.moscase.shouhuan.utils;

/**
 * Created by 陈航 on 2017/10/17.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class NumUtil {
    public static String NumberFormat(float f,int m){
        return String.format("%."+m+"f",f);
    }

    public static float NumberFormatFloat(float f,int m){
        String strfloat = NumberFormat(f,m);
        return Float.parseFloat(strfloat);
    }
}
