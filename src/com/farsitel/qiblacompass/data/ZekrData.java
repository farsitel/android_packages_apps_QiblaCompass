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

public class ZekrData {
    private long id;
    private int zekrCount;
    private int doneZekr;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getZekrCount() {
        return zekrCount;
    }
    public void setZekrCount(int zekrCount) {
        this.zekrCount = zekrCount;
    }
    public int getDoneZekr() {
        return doneZekr;
    }
    public void setDoneZekr(int doneZekr) {
        this.doneZekr = doneZekr;
    }
    public ZekrData(long id, int zekrCount, int doneZekr) {
        super();
        this.id = id;
        this.zekrCount = zekrCount;
        this.doneZekr = doneZekr;
    }

}
