package com.moscase.shouhuan.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by 陈航 on 2017/9/18.
 * <p>
 * 少年一世能狂，敢骂天地不仁
 */

public class MyInfoBean extends DataSupport{
    private String sex;

    private int shengao;

    private int tizhong;

    private int tunwei;

    private int yaowei;

    private int birthday;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getShengao() {
        return shengao;
    }

    public void setShengao(int shengao) {
        this.shengao = shengao;
    }

    public int getTizhong() {
        return tizhong;
    }

    public void setTizhong(int tizhong) {
        this.tizhong = tizhong;
    }

    public int getTunwei() {
        return tunwei;
    }

    public void setTunwei(int tunwei) {
        this.tunwei = tunwei;
    }

    public int getYaowei() {
        return yaowei;
    }

    public void setYaowei(int yaowei) {
        this.yaowei = yaowei;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }
}
