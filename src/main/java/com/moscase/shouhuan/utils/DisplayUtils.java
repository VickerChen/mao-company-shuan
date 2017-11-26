package com.moscase.shouhuan.utils;

import android.content.Context;

/**
 * Created by 陈航 on 2017/7/22.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class DisplayUtils {

    public static int dip2px(Context context, float dip) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    public static int px2dip(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
}
