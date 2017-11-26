package com.moscase.shouhuan.view;


/**
 * Created by 陈航 on 2017/7/30.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
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
