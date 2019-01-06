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
 *
 * 注：只用来计算公制
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

    private double shengao = DataSupport.find(MyInfoBean.class, 1).getShengao();

    private double tizhong = DataSupport.find(MyInfoBean.class, 1).getTizhong();

    private double tunwei = DataSupport.find(MyInfoBean.class, 1).getTunwei();

    private double yaowei = DataSupport.find(MyInfoBean.class, 1).getYaowei();

    private int birthday = DataSupport.find(MyInfoBean.class, 1).getBirthday();


    private ComputeMyInfo(){

    }

    public void setSex(String sex) {
        this.sex = sex;
        ContentValues values = new ContentValues();
        values.put("sex", sex);
        DataSupport.update(MyInfoBean.class, values, 1);
        Log.d("koma", "性别重新设置");
    }

    public void setShengao(double shengao) {
        this.shengao = shengao;
        Log.d("koma", "身高重新设置" + shengao);
        ContentValues values = new ContentValues();
        values.put("shengao", shengao);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setTizhong(double tizhong) {
        this.tizhong = tizhong;
        Log.d("koma", "体重重新设置" + tizhong);
        ContentValues values = new ContentValues();
        values.put("tizhong", tizhong);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setTunwei(double tunwei) {
        this.tunwei = tunwei;
        Log.d("koma", "臀围重新设置" + tunwei);
        ContentValues values = new ContentValues();
        values.put("tunwei", tunwei);
        DataSupport.update(MyInfoBean.class, values, 1);
    }

    public void setYaowei(double yaowei) {
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
            Log.d("koma", "体脂率---a" + a);
            Log.d("koma", "体脂率---b" + b);
            Log.d("koma", "体脂率---c" + b);
            Log.d("koma", "体脂率---男" + d);
            if (d < 0)
                d = 0;
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
            Log.d("koma", "体脂率---女腰围" + yaowei);
            Log.d("koma", "体脂率---女" + d);
            if (d < 0)
                d = 0;
            return (float) d;
        }
    }

    public float computeJichudaixielv() {
        if (sex.equals("男")) {
            double a = 66 + (13.7 * tizhong) + (5 * shengao) - (6.8 * (Calendar.getInstance().get(Calendar.YEAR) - DataSupport.find(MyInfoBean.class, 1).getBirthday()));
            Log.d("koma", "基础代谢男男---" + a);
            Log.d("koma", "基础代谢男男---体重" + tizhong+"身高"+shengao+"年龄"+(Calendar.getInstance().get(Calendar.YEAR) - DataSupport.find(MyInfoBean.class, 1).getBirthday()));
            return (float) a;
        } else {
            double a = 655 + (9.6 * tizhong) + (1.7 * shengao) - (4.7 * (Calendar.getInstance().get(Calendar.YEAR) - DataSupport.find(MyInfoBean.class, 1).getBirthday()));
            Log.d("koma", "基础代谢女女---" + a);
            return (float) a;
        }
    }

    public double computeQuzhitizhong() {
        double a = tizhong - (tizhong * computeTizhilv());
        Log.d("koma", "去脂体重---" + a);
        return a;
    }

    public double computeYaotunbi() {
        double a = yaowei / tunwei * 100;
        Log.d("koma", "腰臀比---" + a);
        return a;
    }

    public float computeShuifen() {
        double a = 1.369 + 0.597 * shengao * shengao / 290 + 0.099 * tizhong - 0.009 * (Calendar.getInstance().get(Calendar.YEAR) - DataSupport.find(MyInfoBean.class, 1).getBirthday());
        Log.d("koma", "水份---" + a);
        return (float) a;
    }

    public int getBirthday(){
        return (Calendar.getInstance().get(Calendar.YEAR) - DataSupport.find(MyInfoBean.class, 1).getBirthday());
    }

}
