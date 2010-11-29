package com.farsitel.android.qiblacompass.data;

public class DirectionData {
    private double northDirectionFromDeviceHead;
    private double qiblaDirectionFromNorth;

    public double getNorthDirectionFromDeviceHead() {
        return northDirectionFromDeviceHead;
    }

    public void setNorthDirectionFromDeviceHead(double northDirectionDegree) {
        this.northDirectionFromDeviceHead = northDirectionDegree;
    }

    public double getQiblaDirectionFromNorth() {
        return qiblaDirectionFromNorth;
    }

    public void setQiblaDirectionFromNorth(double qiblaDirectionFromNorth) {
        this.qiblaDirectionFromNorth = qiblaDirectionFromNorth;
    }
    
    public double getQiblaDirectionFromDeviceHead(){
        return this.northDirectionFromDeviceHead +this.qiblaDirectionFromNorth;
    }

    public DirectionData(double northDirectionFromDeviceHead,
            double qiblaDirectionFromNorth) {
        super();
        this.northDirectionFromDeviceHead = northDirectionFromDeviceHead;
        this.qiblaDirectionFromNorth = qiblaDirectionFromNorth;
    }

}
