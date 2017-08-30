package com.moscase.shouhuan.view;


/**
 * Created by 陈航 on 2017/7/30.
 *
 * 少年一事能狂  敢骂天地不仁
 */
public class RadarData {

    private String title;
    private double percentage;

    public RadarData(String title, double percentage) {
        this.title = title;
        this.percentage = percentage;
    }

    public String getTitle() {
        return title;
    }

    public double getPercentage() {
        return percentage;
    }
}
