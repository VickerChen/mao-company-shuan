package com.moscase.shouhuan.utils;

import android.content.pm.PackageManager;

/**
 * Created by 陈航 on 2017/8/20
 *
 * 对于6.0以上的系统用来检查权限
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public abstract class PermissionUtil {

    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}