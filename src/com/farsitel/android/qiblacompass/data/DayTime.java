package com.farsitel.android.qiblacompass.data;

public class DayTime {
    private int hour;
    private int minute;
    public DayTime(int hour, int minute, int second) {
        super();
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    private int second;
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getSecond() {
        return second;
    }
    public void setSecond(int second) {
        this.second = second;
    }
    @Override
    public String toString(){
        return ""+getHour()+":" +getMinute()+":"+getSecond();
    }
}
