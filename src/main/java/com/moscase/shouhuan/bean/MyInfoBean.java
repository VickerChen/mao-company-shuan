package com.moscase.shouhuan.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by 陈航 on 2017/9/18.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class MyInfoBean extends DataSupport{
    private String sex;

    private double shengao;

    private double tizhong;

    private double tunwei;

    private double yaowei;

    private int birthday;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getShengao() {
        return shengao;
    }

    public void setShengao(double shengao) {
        this.shengao = shengao;
    }

    public double getTizhong() {
        return tizhong;
    }

    public void setTizhong(double tizhong) {
        this.tizhong = tizhong;
    }

    public double getTunwei() {
        return tunwei;
    }

    public void setTunwei(double tunwei) {
        this.tunwei = tunwei;
    }

    public double getYaowei() {
        return yaowei;
    }

    public void setYaowei(double yaowei) {
        this.yaowei = yaowei;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }
}
