package com.moscase.shouhuan.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/5.
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

    public void setId(int id) {
        this.id = id;
    }
}