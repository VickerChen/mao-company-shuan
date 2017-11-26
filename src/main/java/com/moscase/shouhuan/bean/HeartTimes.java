package com.moscase.shouhuan.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by 陈航 on 2017/7/5.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public class HeartTimes extends DataSupport {

    private long id;

    private String time;

    private String times;

    private Date mDate;



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
