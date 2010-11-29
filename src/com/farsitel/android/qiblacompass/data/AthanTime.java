package com.farsitel.android.qiblacompass.data;

import java.util.Date;

public class AthanTime {
    private DayTime fajr = null;
    private DayTime sunrise = null;
    private DayTime dhuhr = null;
    private DayTime asr = null;
    private DayTime sunset = null;
    private DayTime maghrib = null;
    private DayTime isha = null;

    public DayTime getFajr() {
        return fajr;
    }

    public void setFajr(DayTime  fajr) {
        this.fajr = fajr;
    }

    public DayTime  getSunrise() {
        return sunrise;
    }

    public void setSunrise(DayTime  sunrise) {
        this.sunrise = sunrise;
    }

    public DayTime  getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(DayTime  dhuhr) {
        this.dhuhr = dhuhr;
    }

    public DayTime  getAsr() {
        return asr;
    }

    public void setAsr(DayTime  asr) {
        this.asr = asr;
    }

    public DayTime  getSunset() {
        return sunset;
    }

    public void setSunset(DayTime  sunset) {
        this.sunset = sunset;
    }

    public DayTime  getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(DayTime  maghrib) {
        this.maghrib = maghrib;
    }

    public DayTime  getIsha() {
        return isha;
    }

    public void setIsha(DayTime  isha) {
        this.isha = isha;
    }

}
