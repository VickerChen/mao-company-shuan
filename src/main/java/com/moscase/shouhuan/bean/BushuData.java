package com.moscase.shouhuan.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by 陈航 on 2017/10/24.
 *
 * 步数的bean
 *
 */

public class BushuData extends DataSupport {

    private String riqi;

    private int bushu;

    public String getRiqi() {
        return riqi;
    }

    public void setRiqi(String riqi) {
        this.riqi = riqi;
    }

    public int getBushu() {
        return bushu;
    }

    public void setBushu(int bushu) {
        this.bushu = bushu;
    }
}
