package com.moscase.shouhuan.utils;

import android.content.ContentValues;
import android.util.Log;

import com.moscase.shouhuan.bean.MyInfoBean;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

/**
 * Created by 陈航 on 2017/9/13.
 * <p>
 * 计算各种指标，详细信息参考项目根目录下的PDF
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class ComputeMyInfo {


    public static ComputeMyInfo mComputeMyInfo;


    public static synchronized ComputeMyInfo getInstance() {
        if (mComputeMyInfo == null) {
            mComputeMyInfo = new ComputeMyInfo();
        }
        return mComputeMyInfo;
    }

    private String sex = DataSupport.find(MyInfoBean.class, 1).getSex();

    private int shengao = DataSupport.find(MyInfoBean.class, 1).getShengao();

    private int tizhong = DataSupport.find(MyInfoBean.class, 1).getTizhong();

    private int tunwei = DataSupport.find(MyInfoBean.class, 1).getTunwei();

    private int yaowei = DataSupport.find(MyInfoBean.class, 1).getYaowei();

    private int birthday = DataSupport.find(MyInfoBean.class, 1).getBirthday();

    public void setSex(String sex) {
        this.sex = sex;
        ContentValues values = new ContentValues();
        values.put("sex", sex);
        DataSupport.update(MyInfoBean.class, values, 1);
        Log.d("koma", "性别重新设置");
    }

    public void setShengao(int shengao) {
        this.shengao = shengao;
        Log.d("koma", "身高重新设置" + shengao);
        ContentValues values = new ContentValues();
        values.put("shengao", shengao);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setTizhong(int tizhong) {
        this.tizhong = tizhong;
        Log.d("koma", "体重重新设置" + tizhong);
        ContentValues values = new ContentValues();
        values.put("tizhong", tizhong);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setTunwei(int tunwei) {
        this.tunwei = tunwei;
        Log.d("koma", "臀围重新设置" + tunwei);
        ContentValues values = new ContentValues();
        values.put("tunwei", tunwei);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setYaowei(int yaowei) {
        this.yaowei = yaowei;
        Log.d("koma", "腰围重新设置" + this.yaowei);
        ContentValues values = new ContentValues();
        values.put("yaowei", yaowei);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
        Log.d("koma", "生日重新设置");
        ContentValues values = new ContentValues();
        values.put("birthday", birthday);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    private int yearsOld = (Calendar.getInstance().get(Calendar.YEAR) - birthday);


    @Override
    public String toString() {
        return "ComputeMyInfo{" +
                "sex='" + sex + '\'' +
                ", shengao=" + shengao +
                ", tizhong=" + tizhong +
                ", tunwei=" + tunwei +
                ", yaowei=" + yaowei +
                ", yearsOld=" + (Calendar.getInstance().get(Calendar.YEAR) - birthday) +
                '}';
    }


    public float computeBMI() {
        float BMI = (float) Math.rint(tizhong / ((shengao / 1.0f / 100) * (shengao / 1.0f / 100)));
        Log.d("koma", "BMI---" + BMI);
        return BMI;
    }

    public float computeTizhilv() {
        if (sex.equals("男")) {
            //参数a = 腰围公分（腰部的周长）*0.74
            double a = yaowei * 0.74;
            //参数b = （总体重公斤*0.082）+ 44.74
            double b = tizhong * 0.082 + 44.74;
            //身体脂肪总重量公斤= a - b
            double c = a - b;
            //身体脂肪百分比 = （身体脂肪总重量/体重）* 100%
            double d = c / tizhong;
            Log.d("koma", "体脂率---男" + d);
            return (float) d;
        } else {
            //参数a = 腰围公分（腰部的周长）*0.74
            double a = yaowei * 0.74;
            //参数b = （总体重公斤*0.082）+ 34.89
            double b = tizhong * 0.082 + 34.89;
            //身体脂肪总重量公斤= a - b
            double c = a - b;
            //身体脂肪百分比 = （身体脂肪总重量/体重）* 100%
            double d = c / tizhong;
            Log.d("koma", "体脂率---女" + d);
            return (float) d;
        }
    }

    public float computeJichudaixielv() {
        if (sex.equals("男")) {
            double a = 66 + (13.7 * tizhong) + (5 * shengao) - (6.8 * yearsOld);
            Log.d("koma", "基础代谢---" + a);
            return (float) a;
        } else {
            double a = 655 + (9.6 * tizhong) + (1.7 * shengao) - (4.7 * yearsOld);
            Log.d("koma", "基础代谢---" + a);
            return (float) a;
        }
    }

    public float computeQuzhitizhong() {
        float a = tizhong - (tizhong * computeTizhilv());
        Log.d("koma", "去脂体重---" + a);
        return a;
    }

    public float computeYaotunbi() {
        float a = (float) yaowei / tunwei * 100;
        Log.d("koma", "腰臀比---" + a);
        return a;
    }

    public float computeShuifen() {
        double a = 1.369 + 0.597 * shengao * shengao / 290 + 0.099 * tizhong - 0.009 * yearsOld;
        Log.d("koma", "水份---" + a);
        return (float) a;
    }

}
