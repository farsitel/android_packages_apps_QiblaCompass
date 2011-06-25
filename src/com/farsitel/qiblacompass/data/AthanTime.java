/*
 * Copyright (C) 2011 Iranian Supreme Council of ICT, The FarsiTel Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASICS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farsitel.qiblacompass.data;

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
