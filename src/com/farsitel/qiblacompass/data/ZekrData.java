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
