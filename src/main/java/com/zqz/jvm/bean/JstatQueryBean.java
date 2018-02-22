package com.zqz.jvm.bean;

/**
 * Created by sangjian on 2018/2/14.
 */
public class JstatQueryBean {

    private String userId;

    private long jvmId;

    private String option;

    private int interval;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    private int times;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getJvmId() {
        return jvmId;
    }

    public void setJvmId(long jvmId) {
        this.jvmId = jvmId;
    }
}
